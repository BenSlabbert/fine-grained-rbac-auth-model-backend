/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.Set;
import org.example.iam.PostgresTestBase;
import org.example.iam.seeder.ApiSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MerchantHandlerIT extends PostgresTestBase {

  private ApiSeeder seeder;

  @BeforeEach
  void init(Vertx v) {
    seeder = new ApiSeeder(v, getPort());
  }

  @Test
  void createMerchant(VertxTestContext tc) {
    seeder.createMerchant(b -> b.name("m1")).onComplete(tc.succeedingThenComplete());
  }

  @Test
  void createMerchantGroup(VertxTestContext tc) {
    seeder.createMerchantGroup(b -> b.name("mg1")).onComplete(tc.succeedingThenComplete());
  }

  @Test
  void createCustomMerchantGroup(VertxTestContext tc) {
    seeder.createCustomMerchantGroup(b -> b.name("cmg1")).onComplete(tc.succeedingThenComplete());
  }

  @Test
  void addMerchantToCustomMerchantGroup(VertxTestContext tc) {
    seeder
        .createCustomMerchantGroup(b -> b.name("cmg1"))
        .compose(_ -> seeder.createMerchant(b -> b.name("m1")))
        .compose(
            _ ->
                seeder.addMerchantToCustomMerchantGroup(
                    b -> b.customMerchantGroupName("cmg1").merchantNames(Set.of("m1"))))
        .onComplete(tc.succeedingThenComplete());
  }
}
