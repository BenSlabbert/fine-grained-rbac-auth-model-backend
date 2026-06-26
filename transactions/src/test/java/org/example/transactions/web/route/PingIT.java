/* Licensed under Apache-2.0 2026. */
package org.example.transactions.web.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import org.example.security.api.*;
import org.example.transactions.PostgresTestBase;
import org.example.transactions.config.TransactionsConfig;
import org.junit.jupiter.api.Test;

class PingIT extends PostgresTestBase {

  @Test
  void test(Vertx v, VertxTestContext tc) {
    SecurityService securityService = mock(SecurityService.class);
    var proxyHandler = SecurityServiceVertxEBProxyHandler_Factory.newInstance(v, securityService);
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

    String token = getToken("username");

    getWebClient(v)
        .get("/ping")
        .authentication(new TokenCredentials(token).applyHttpChallenge(null))
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
