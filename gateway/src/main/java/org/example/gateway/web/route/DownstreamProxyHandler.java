/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import static java.util.stream.Collectors.toMap;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.All;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.example.gateway.config.GatewayConfig;

@WebHandler(path = "/api")
class DownstreamProxyHandler {

  private final Map<String, Service> serviceMap;
  private final HttpClientAgent httpClient;
  private final JWTAuth provider;

  @Inject
  DownstreamProxyHandler(Vertx vertx, GatewayConfig gatewayConfig) {
    this.serviceMap =
        gatewayConfig.services().stream()
            .collect(toMap(GatewayConfig.Service::name, s -> new Service(s.host(), s.port())));
    this.provider =
        JWTAuth.create(
            vertx,
            new JWTAuthOptions()
                .addPubSecKey(
                    new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setId("simple")
                        .setBuffer(gatewayConfig.jwt().secret())));
    this.httpClient = vertx.createHttpClient();
  }

  private record Service(String host, int port) {}

  @HasRole("admin")
  @All(path = "/{string:serviceName}/*")
  void all(@WebRequest.RoutingContext RoutingContext ctx) {
    var pathParams = DownstreamProxyHandler_All_ParamParser.parse(ctx.pathParams());
    HttpServerRequest request = ctx.request();
    Service service = serviceMap.get(pathParams.serviceName());
    if (null == service) {
      ctx.response().setStatusCode(401).end();
      return;
    }

    // find a downstream service for this context
    // generate a JWT token for this call
    // execute the call
    // send response back to the client
    request.pause();
    var requestOptions =
        getRequestOptions(ctx.user().subject(), pathParams.serviceName(), request, service);

    httpClient
        .request(requestOptions)
        .compose(
            r -> {
              request.resume();
              return request.body().onFailure(VertxException::noStackTrace).compose(r::send);
            })
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

  private RequestOptions getRequestOptions(
      String subject, String serviceName, HttpServerRequest request, Service service) {
    String uri = request.uri();
    String path = uri.substring(("/api/" + serviceName).length());
    return new RequestOptions()
        .setMethod(request.method())
        .setHost(service.host)
        .setPort(service.port)
        .setURI(path)
        .setHeaders(request.headers())
        .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getToken(subject, serviceName));
  }

  private String getToken(String subject, String serviceName) {
    // https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims#registered-claims
    return provider.generateToken(
        new JsonObject()
            // JWT ID
            .put("jti", UUID.randomUUID().toString())
            // not before time
            .put("nbf", System.currentTimeMillis()),
        new JWTOptions()
            .setIgnoreExpiration(false)
            .setLeeway(100)
            .setSubject(subject)
            .setExpiresInSeconds(30)
            .setIssuer("gateway")
            // iam can view the token as well
            .setAudience(Set.of(serviceName, "iam").stream().toList()));
  }
}
