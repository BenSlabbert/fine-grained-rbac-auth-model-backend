/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.impl.jose.JWT;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import java.security.SignatureException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.example.gateway.IntegrationTestBase;
import org.example.gateway.config.GatewayConfig;
import org.example.gateway.config.GatewayConfigBuilder;
import org.example.gateway.config.GatewayConfig_JwtBuilder;
import org.example.gateway.config.GatewayConfig_ServiceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DownstreamProxyHandlerTest extends IntegrationTestBase {

  private static final String SECRET = "secret";
  private volatile HttpServer server;

  @Override
  @BeforeEach
  protected void init(Vertx v, VertxTestContext tc) {
    Router r = Router.router(v);
    r.route(HttpMethod.GET, "/p1")
        .handler(
            ctx -> {
              String jwtHeader = ctx.request().getHeader(HttpHeaders.AUTHORIZATION);
              assertThat(jwtHeader).isNotNull();
              String token = jwtHeader.replace("Bearer ", "");
              String[] split = token.split("\\.");
              assertThat(split).hasSize(3);
              token = split[0] + "." + split[1];

              try {
                JWT jwt = new JWT();
                JsonObject decode = jwt.decode(token);
                assertThat(decode).isNotNull();
                assertThat(decode.fieldNames())
                    .containsExactlyInAnyOrder("jti", "nbf", "iat", "exp", "aud", "iss", "sub");
                assertThat(decode.getString("iss")).isEqualTo("gateway");
                assertThat(decode.getString("sub")).isEqualTo("name");
                assertThat(decode.getJsonArray("aud")).containsExactlyInAnyOrder("test", "iam");
              } catch (SignatureException e) {
                tc.failNow(e);
              }

              assertThat(ctx.request().getHeader("X-custom-req")).isEqualTo("req-header");
              ctx.response().putHeader("X-custom", "value").setStatusCode(200).end("body");
            });
    v.createHttpServer(new HttpServerOptions().setPort(0).setHost("0.0.0.0"))
        .requestHandler(r)
        .listen()
        .onComplete(
            tc.succeeding(
                ar -> {
                  server = ar;
                  GatewayConfig gatewayConfig =
                      GatewayConfigBuilder.builder()
                          .jwt(
                              GatewayConfig_JwtBuilder.builder()
                                  .secret(Buffer.buffer(SECRET))
                                  .build())
                          .services(
                              List.of(
                                  GatewayConfig_ServiceBuilder.builder()
                                      .name("test")
                                      .host("127.0.0.1")
                                      .port(server.actualPort())
                                      .build()))
                          .build();
                  deployVerticle(v, tc, gatewayConfig);
                }));
  }

  @AfterEach
  void after(VertxTestContext tc) {
    if (null != server) {
      server.close().onComplete(tc.succeedingThenComplete());
    }
  }

  @Test
  void test(Vertx v, VertxTestContext tc) {
    Checkpoint loginCheckpoint = tc.checkpoint();
    Checkpoint testCompleteCheckpoint = tc.checkpoint();

    AtomicReference<String> cookie = new AtomicReference<>();

    WebClient webClient = getWebClient(v);
    webClient
        .post("/login")
        .authentication(ADMIN_AUTH)
        .send()
        .onComplete(
            tc.succeeding(
                resp ->
                    tc.verify(
                        () -> {
                          assertThat(resp.statusCode()).isEqualTo(200);
                          String header = resp.getHeader(HttpHeaders.SET_COOKIE);
                          cookie.set(header);
                          loginCheckpoint.flag();
                        })));

    loginCheckpoint.await();

    webClient
        .get("/api/test/p1")
        .putHeader(HttpHeaders.COOKIE, cookie.get())
        .putHeader("X-custom-req", "req-header")
        .send()
        .onComplete(
            tc.succeeding(
                resp ->
                    tc.verify(
                        () -> {
                          assertThat(resp.statusCode()).isEqualTo(200);
                          assertThat(resp.bodyAsString()).isEqualTo("body");
                          assertThat(resp.headers().get("X-custom")).isEqualTo("value");
                          testCompleteCheckpoint.flag();
                        })));
  }
}
