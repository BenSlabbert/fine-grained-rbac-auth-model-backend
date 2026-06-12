/* Licensed under Apache-2.0 2026. */
package org.example.iam.eb;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.junit5.VertxTestContext;
import org.example.iam.PostgresTestBase;
import org.example.security.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityServiceImplIT extends PostgresTestBase {

  private SecurityService securityService;

  @BeforeEach
  void setUp(Vertx v) {
    var opts = new DeliveryOptions();
    securityService = new SecurityServiceVertxEBClientProxy(v, opts);
  }

  @Test
  void test(VertxTestContext tc) {
    securityService
        .hasPermission(
            HasPermissionRequestBuilder.builder()
                .application("application")
                .user("user")
                .permission("permisssion")
                .build())
        .onComplete(
            tc.succeeding(
                r ->
                    tc.verify(
                        () -> {
                          assertThat(r.hasPermission()).isFalse();
                          tc.completeNow();
                        })));
  }
}
