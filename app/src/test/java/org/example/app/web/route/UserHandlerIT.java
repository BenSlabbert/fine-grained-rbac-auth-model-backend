/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.example.app.PostgresTestBase;
import org.junit.jupiter.api.Test;

class UserHandlerIT extends PostgresTestBase {

  @Test
  void test(Vertx v, VertxTestContext tc) {
    getWebClient(v)
        .post("/user")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(
            CreateUserRequestJson.toJson(CreateUserRequestBuilder.builder().name("u1").build()))
        .onComplete(
            tc.succeeding(
                r ->
                    tc.verify(
                        () -> {
                          assertThat(r.statusCode()).isEqualTo(201);
                          tc.completeNow();
                        })));
  }
}
