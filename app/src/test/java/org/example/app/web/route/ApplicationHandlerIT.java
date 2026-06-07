/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.Set;
import org.example.app.PostgresTestBase;
import org.example.app.seeder.ApiSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationHandlerIT extends PostgresTestBase {

  private ApiSeeder seeder;

  @BeforeEach
  void init(Vertx v) {
    seeder = new ApiSeeder(v, getPort());
  }

  @Test
  void test(VertxTestContext tc) {
    seeder
        .createApplication(b -> b.name("a1"))
        .compose(_ -> seeder.createApplication(b -> b.name("a2")))
        .compose(
            _ ->
                seeder.createPermission(
                    b -> b.applicationName("a1").permissions(Set.of("a1:p1", "a1:p2"))))
        .compose(
            _ -> seeder.createPermission(b -> b.applicationName("a2").permissions(Set.of("a2:p1"))))
        .compose(_ -> seeder.getApplicationPermissions("a1"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.permissions())
                              .containsExactlyInAnyOrder("a1:p1", "a1:p2");
                          tc.completeNow();
                        })));
  }
}
