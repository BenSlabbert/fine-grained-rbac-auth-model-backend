/* Licensed under Apache-2.0 2026. */
package org.example.transactions.web.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import org.example.security.api.ApplicationUserPermissionsResponseBuilder;
import org.example.security.api.SecurityService;
import org.example.security.api.SecurityServiceVertxEBProxyHandler_Factory;
import org.example.transactions.PostgresTestBase;
import org.junit.jupiter.api.Test;

class PingIT extends PostgresTestBase {

  @Test
  void test(Vertx v, VertxTestContext tc) {
    SecurityService securityService = mock(SecurityService.class);
    var proxyHandler = SecurityServiceVertxEBProxyHandler_Factory.newInstance(v, securityService);
    proxyHandler.register();

    when(securityService.getApplicationUserPermissions(any()))
        .thenReturn(
            Future.succeededFuture(
                ApplicationUserPermissionsResponseBuilder.builder()
                    .permissions(List.of("admin"))
                    .build()));

    getWebClient(v)
        .get("/ping")
        .authentication(ADMIN_AUTH)
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
