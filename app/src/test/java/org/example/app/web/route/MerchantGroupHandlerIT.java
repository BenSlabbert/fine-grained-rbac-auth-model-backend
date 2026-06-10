/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.Set;
import org.example.app.PostgresTestBase;
import org.example.app.seeder.ApiSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MerchantGroupHandlerIT extends PostgresTestBase {

  private ApiSeeder seeder;

  @BeforeEach
  void init(Vertx v) {
    seeder = new ApiSeeder(v, getPort());
  }

  @Test
  void addMerchantToMerchantGroup(VertxTestContext tc) {
    seeder
        .createMerchantGroup(b -> b.name("mg1"))
        .compose(_ -> seeder.createMerchant(b -> b.name("m1")))
        .compose(
            _ ->
                seeder.addMerchantToMerchantGroup(
                    b -> b.merchantGroupName("mg1").merchantNames(Set.of("m1"))))
        .onComplete(tc.succeedingThenComplete());
  }
}
