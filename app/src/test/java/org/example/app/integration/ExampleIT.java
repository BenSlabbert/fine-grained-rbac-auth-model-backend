/* Licensed under Apache-2.0 2026. */
package org.example.app.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

class ExampleIT extends PostgresTestBase {

  @Test
  void test(Vertx vertx, VertxTestContext testContext) {
    getWebClient(vertx)
        .get("/ping")
        .basicAuthentication("name", "password")
        .send()
        .onComplete(testContext.succeeding(r -> assertThat(r.bodyAsString()).isEqualTo("pong")));
  }
}
