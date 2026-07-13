/* Licensed under Apache-2.0 2026. */
package org.example.transactions.web.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.auth.impl.jose.JWT;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.AuthenticationInterceptor;
import io.vertx.serviceproxy.AuthorizationInterceptor;
import java.util.List;
import org.example.security.api.*;
import org.example.transactions.PostgresTestBase;
import org.example.transactions.config.TransactionsConfig;
import org.junit.jupiter.api.Test;

class PingIT extends PostgresTestBase {

  @Test
  void test(Vertx v, VertxTestContext tc) {
    SecurityService securityService = mock(SecurityService.class);

    AuthenticationInterceptor authenticationInterceptor =
        AuthenticationInterceptor.create(
            credentials ->
                switch (credentials) {
                  case TokenCredentials tokenC -> {
                    String token = tokenC.getToken();
                    assertThat(token).startsWith("ey");
                    JsonObject parse = JWT.parse(token);
                    JsonObject payload = parse.getJsonObject("payload");
                    String sub = payload.getString("sub");
                    assertThat(sub).startsWith("transactions");
                    yield Future.succeededFuture(User.fromName("name"));
                  }
                  case null -> throw new IllegalStateException("cannot be null");
                  default -> throw new IllegalStateException("Unexpected value: " + credentials);
                });

    AuthorizationInterceptor authorizationInterceptor =
        AuthorizationInterceptor.create(
            new AuthorizationProvider() {
              @Override
              public String getId() {
                return "test";
              }

              @Override
              public Future<Void> getAuthorizations(User user) {
                user.authorizations().put(getId(), RoleBasedAuthorization.create("system"));
                return Future.succeededFuture();
              }
            });

    var proxyHandler =
        SecurityServiceVertxEBProxyHandler_Factory.newInstance(
            v, securityService, authenticationInterceptor, () -> authorizationInterceptor);
    proxyHandler.register();

    when(securityService.getApplicationUserPermissions(
            ApplicationUserPermissionsRequestBuilder.builder()
                .user("username")
                .application(TransactionsConfig.APP_NAME)
                .build()))
        .thenReturn(
            Future.succeededFuture(
                ApplicationUserPermissionsResponseBuilder.builder()
                    .permissions(List.of("admin"))
                    .build()));

    getWebClient(v)
        .get("/ping")
        .authentication(new TokenCredentials(getApiToken("username")).applyHttpChallenge(null))
        .send()
        .onComplete(
            tc.succeeding(
                r ->
                    tc.verify(
                        () -> {
                          assertThat(r.bodyAsString()).isEqualTo("pong\n");
                          tc.completeNow();
                        })));
  }
}
