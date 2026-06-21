/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import static java.util.stream.Collectors.toMap;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.All;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.WebServerRequest;
import io.vertx.httpproxy.*;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.example.gateway.config.GatewayConfig;

@WebHandler(path = "")
class DownstreamProxyHandler {

  private static final String API_PATH = "/api";
  private static final String USER_CTX = "user";
  private static final String SERVICE_CTX = "service";
  private static final String SERVICE_NAME_CTX = "service_name";

  private final Map<String, Service> serviceMap;
  private final Handler<HttpServerRequest> proxy;
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

    this.proxy = getProxy(vertx);
  }

  private HttpProxy getProxy(Vertx vertx) {
    ProxyOptions options =
        new ProxyOptions()
            .setCacheOptions(null) // disable
            .setSupportWebSocket(false)
            .setForwardedHeadersOptions(
                new ForwardedHeadersOptions().setEnabled(true).setUseRfc7239(true));
    return HttpProxy.reverseProxy(options, vertx.createHttpClient())
        .addInterceptor(ProxyInterceptor.builder().removingPathPrefix(API_PATH).build())
        .addInterceptor(
            new ProxyInterceptor() {
              @Override
              public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                WebServerRequest wsr = (WebServerRequest) context.request().proxiedRequest();
                RoutingContext ctx = wsr.routingContext();

                var pathParams = DownstreamProxyHandler_All_ParamParser.parse(ctx.pathParams());

                if (null == ctx.user()) {
                  ProxyResponse proxyResponse =
                      context
                          .request()
                          .response()
                          .setStatusCode(401)
                          .setStatusMessage("Unauthorized")
                          .putHeader("Content-Type", "text/plain")
                          .setBody(Body.body(Buffer.buffer("")));
                  return Future.succeededFuture(proxyResponse);
                }

                Service service = serviceMap.get(pathParams.serviceName());

                if (null == service) {
                  ProxyResponse proxyResponse =
                      context
                          .request()
                          .response()
                          .setStatusCode(401)
                          .setStatusMessage("Unauthorized")
                          .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
                          .setBody(Body.body(Buffer.buffer()));
                  return Future.succeededFuture(proxyResponse);
                }

                context.set(USER_CTX, ctx.user());
                context.set(SERVICE_CTX, service);
                context.set(SERVICE_NAME_CTX, pathParams.serviceName());

                return context.sendRequest();
              }
            })
        .addInterceptor(
            new ProxyInterceptor() {
              @Override
              public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                String s = context.get(SERVICE_NAME_CTX, String.class);
                return ProxyInterceptor.builder()
                    .removingPathPrefix("/%s".formatted(s))
                    .build()
                    .handleProxyRequest(context);
              }
            })
        .origin(
            OriginRequestProvider.selector(
                proxyContext -> {
                  var user = proxyContext.get(USER_CTX, User.class);
                  var service = proxyContext.get(SERVICE_CTX, Service.class);
                  var serviceName = proxyContext.get(SERVICE_NAME_CTX, String.class);
                  String token = getToken(user.subject(), serviceName);
                  proxyContext.request().putHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                  var origin = SocketAddress.inetSocketAddress(service.port, service.host);
                  return Future.succeededFuture(origin);
                }));
  }

  private record Service(String host, int port) {}

  @HasRole("admin")
  @All(path = API_PATH + "/{string:serviceName}/*")
  void all(@WebRequest.RoutingContext RoutingContext ctx) {
    proxy.handle(ctx.request());
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
