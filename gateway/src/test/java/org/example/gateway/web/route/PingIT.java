/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.example.gateway.IntegrationTestBase;
import org.junit.jupiter.api.Test;

class PingIT extends IntegrationTestBase {

  @Test
  void test(Vertx vertx, VertxTestContext tc) {
    WebClient wc = getWebClient(vertx);

    wc.get("/ping")
        .send()
        .onComplete(
            tc.succeeding(
                r ->
                    tc.verify(
                        () -> {
                          assertThat(r.statusCode()).isEqualTo(200);
                          tc.completeNow();
                        })));
  }
}
