/* Licensed under Apache-2.0 2026. */
package org.example.app.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

class PingIT extends PostgresTestBase {

  @Test
  void test(Vertx v, VertxTestContext tc) {
    getWebClient(v)
        .get("/ping")
        .basicAuthentication("name", "password")
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
