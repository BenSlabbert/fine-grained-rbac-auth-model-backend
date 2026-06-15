/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.All;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@WebHandler(path = "/api")
class DownstreamProxyHandler {

  private final Map<String, Service> serviceMap;
  private final Vertx vertx;

  @Inject
  DownstreamProxyHandler(Vertx vertx) {
    this.serviceMap =
        Map.of(
            "transactions", new Service("127.0.0.1", 8082), "iam", new Service("127.0.0.1", 8002));
    this.vertx = vertx;
  }

  private record Service(String host, int port) {}

  @All(path = "/{string:serviceName}/*")
  void all(@WebRequest.RoutingContext RoutingContext ctx) {
    var pathParams = DownstreamProxyHandler_All_ParamParser.parse(ctx.pathParams());
    User user = ctx.user();
    if (null == user) {
      // not authenticated
      ctx.response().setStatusCode(401).end();
      return;
    }
    String uri = ctx.request().uri();
    Service service = serviceMap.get(pathParams.serviceName());
    if (null == service) {
      ctx.response().setStatusCode(401).end();
      return;
    }

    String path = uri.substring(uri.indexOf('/'));
    // find a downstream service for this context
    // generate a JWT token for this call
    // execute the call
    // send response back to the client
    JWTAuth provider =
        JWTAuth.create(
            vertx,
            new JWTAuthOptions()
                .addPubSecKey(
                    new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setId("simple")
                        .setBuffer("keyboard cat")));

    String token =
        // https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims#registered-claims
        provider.generateToken(
            new JsonObject()
                // JWT ID
                .put("jti", UUID.randomUUID().toString())
                // not before time
                .put("nbf", System.currentTimeMillis()),
            new JWTOptions()
                .setIgnoreExpiration(false)
                .setLeeway(100)
                .setSubject(user.subject())
                .setExpiresInSeconds(30)
                .setIssuer("gateway")
                // iam can view the token as well
                .setAudience(Set.of(pathParams.serviceName(), "iam").stream().toList()));

    var requestOptions =
        new RequestOptions()
            .setMethod(ctx.request().method())
            .setHost(service.host)
            .setPort(service.port)
            .setURI(path)
            .setHeaders(ctx.request().headers())
            .addHeader("Bearer ", token);

    vertx
        .createHttpClient()
        .request(requestOptions)
        .compose(r -> ctx.request().body().onFailure(VertxException::noStackTrace).compose(r::send))
        .onComplete(
            ar -> {
              if (ar.failed()) {
                ctx.response().setStatusCode(500).end();
                return;
              }

              HttpClientResponse result = ar.result();
              result.bodyHandler(
                  body -> {
                    HttpServerResponse response = ctx.response();
                    result
                        .headers()
                        .forEach(header -> response.putHeader(header.getKey(), header.getValue()));
                    response.setStatusCode(result.statusCode()).end(body);
                  });
            });
  }
}
