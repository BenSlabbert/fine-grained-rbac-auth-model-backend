/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.Set;
import org.example.app.PostgresTestBase;
import org.example.app.seeder.ApiSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserScopeHandlerIT extends PostgresTestBase {

  private ApiSeeder seeder;

  @BeforeEach
  void init(Vertx v) {
    seeder = new ApiSeeder(v, getPort());
  }

  @Test
  void addUserToPspScope(VertxTestContext tc) {
    seeder
        .createUser(b -> b.name("u1"))
        .compose(_ -> seeder.createPsp(b -> b.name("psp1")))
        .compose(_ -> seeder.addUserToPspScope(b -> b.userName("u1").pspNames(Set.of("psp1"))))
        .onComplete(tc.succeedingThenComplete());
  }

  @Test
  void addUserToMerchantScope(VertxTestContext tc) {
    seeder
        .createUser(b -> b.name("u1"))
        .compose(_ -> seeder.createMerchant(b -> b.name("m1")))
        .compose(
            _ -> seeder.addUserToMerchantScope(b -> b.userName("u1").merchantNames(Set.of("m1"))))
        .onComplete(tc.succeedingThenComplete());
  }

  @Test
  void addUserToMerchantGroupScope(VertxTestContext tc) {
    seeder
        .createUser(b -> b.name("u1"))
        .compose(_ -> seeder.createMerchantGroup(b -> b.name("mg1")))
        .compose(
            _ ->
                seeder.addUserToMerchantGroupScope(
                    b -> b.userName("u1").merchantGroupNames(Set.of("mg1"))))
        .onComplete(tc.succeedingThenComplete());
  }

  @Test
  void addUserToCustomMerchantGroupScope(VertxTestContext tc) {
    seeder
        .createUser(b -> b.name("u1"))
        .compose(_ -> seeder.createCustomMerchantGroup(b -> b.name("cmg1")))
        .compose(
            _ ->
                seeder.addUserToCustomMerchantGroupScope(
                    b -> b.userName("u1").customMerchantGroupNames(Set.of("cmg1"))))
        .onComplete(tc.succeedingThenComplete());
  }
}
