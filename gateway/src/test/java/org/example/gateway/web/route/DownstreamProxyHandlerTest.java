/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.atomic.AtomicReference;
import org.example.gateway.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class DownstreamProxyHandlerTest extends IntegrationTestBase {

  private volatile HttpServer server;

  @AfterEach
  void after(VertxTestContext tc) {
    if (null != server) {
      server.close().onComplete(tc.succeedingThenComplete());
    }
  }

  @Test
  void test(Vertx v, VertxTestContext tc) {
    Checkpoint downstreamServerCheckpoint = tc.checkpoint();
    Checkpoint loginCheckpoint = tc.checkpoint();
    Checkpoint testCompleteCheckpoint = tc.checkpoint();

    var r = Router.router(v);
    r.route(HttpMethod.GET, "/p1")
        .handler(
            ctx -> {
              assertThat(ctx.request().getHeader("X-custom-req")).isEqualTo("req-header");
              ctx.response().putHeader("X-custom", "value").setStatusCode(200).end("body");
            });
    var s =
        v.createHttpServer(new HttpServerOptions().setPort(8082).setHost("0.0.0.0"))
            .requestHandler(r);

    s.listen()
        .onFailure(tc::failNow)
        .onSuccess(
            ar -> {
              server = ar;
              downstreamServerCheckpoint.flag();
            });

    downstreamServerCheckpoint.await();

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
        .get("/api/transactions/p1")
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
