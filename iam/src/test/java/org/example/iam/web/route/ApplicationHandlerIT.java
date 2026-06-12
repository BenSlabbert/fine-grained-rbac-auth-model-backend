/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.Set;
import org.example.iam.PostgresTestBase;
import org.example.iam.seeder.ApiSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationHandlerIT extends PostgresTestBase {

  private ApiSeeder seeder;

  @BeforeEach
  void init(Vertx v) {
    seeder = new ApiSeeder(v, getPort());
  }

  @Test
  void getApplicationPermissions(VertxTestContext tc) {
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

  @Test
  void getApplicationUserPermissions(VertxTestContext tc) {
    permissionPreconditions()
        .compose(_ -> seeder.getApplicationUserPermissions("a1", "u1"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.permissions()).singleElement().isEqualTo("a1:p1");
                          tc.completeNow();
                        })));
  }

  @Test
  void hasPermission(VertxTestContext tc) {
    permissionPreconditions()
        .compose(_ -> seeder.hasPermission("a1", "u1", "a1:p1"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.hasPermission()).isTrue();
                          tc.completeNow();
                        })));
  }

  @Test
  void userHasPspScope(VertxTestContext tc) {
    merchantPermissionsPreconditions()
        .compose(_ -> seeder.userHasPspScope("u1", "psp1"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.hasPermission()).isTrue();
                          tc.completeNow();
                        })));
  }

  @Test
  void userHasMerchantScope_merchant(VertxTestContext tc) {
    merchantPermissionsPreconditions()
        .compose(_ -> seeder.userHasMerchantScope("u1", "m6"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.hasPermission()).isTrue();
                          tc.completeNow();
                        })));
  }

  @Test
  void userHasMerchantScope_merchantGroup(VertxTestContext tc) {
    merchantPermissionsPreconditions()
        .compose(_ -> seeder.userHasMerchantScope("u1", "m4"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.hasPermission()).isTrue();
                          tc.completeNow();
                        })));
  }

  @Test
  void userHasMerchantScope_psp(VertxTestContext tc) {
    merchantPermissionsPreconditions()
        .compose(_ -> seeder.userHasMerchantScope("u1", "m2"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.hasPermission()).isTrue();
                          tc.completeNow();
                        })));
  }

  @Test
  void userHasMerchantScope_customMerchantGroup(VertxTestContext tc) {
    merchantPermissionsPreconditions()
        .compose(_ -> seeder.userHasMerchantScope("u1", "m5"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.hasPermission()).isTrue();
                          tc.completeNow();
                        })));
  }

  @Test
  void userHasMerchantGroupScope(VertxTestContext tc) {
    merchantPermissionsPreconditions()
        .compose(_ -> seeder.userHasMerchantGroupScope("u1", "mg1"))
        .onComplete(
            tc.succeeding(
                result ->
                    tc.verify(
                        () -> {
                          assertThat(result.hasPermission()).isTrue();
                          tc.completeNow();
                        })));
  }

  private Future<Void> merchantPermissionsPreconditions() {
    return seeder
        .createUser(b -> b.name("u1"))
        .compose(_ -> seeder.createUser(b -> b.name("u2")))
        .compose(_ -> seeder.createPsp(b -> b.name("psp1")))
        .compose(_ -> seeder.createPsp(b -> b.name("psp2")))
        .compose(_ -> seeder.createMerchant(b -> b.name("m1")))
        .compose(_ -> seeder.createMerchant(b -> b.name("m2")))
        .compose(_ -> seeder.createMerchant(b -> b.name("m3")))
        .compose(_ -> seeder.createMerchant(b -> b.name("m4")))
        .compose(_ -> seeder.createMerchant(b -> b.name("m5")))
        .compose(_ -> seeder.createMerchant(b -> b.name("m6")))
        .compose(_ -> seeder.createMerchantGroup(b -> b.name("mg1")))
        .compose(_ -> seeder.createMerchantGroup(b -> b.name("mg2")))
        .compose(_ -> seeder.createCustomMerchantGroup(b -> b.name("cmg1")))
        .compose(_ -> seeder.createCustomMerchantGroup(b -> b.name("cmg2")))
        .compose(_ -> seeder.addMerchantToPsp(b -> b.merchantName("m1").pspName("psp1")))
        .compose(_ -> seeder.addMerchantToPsp(b -> b.merchantName("m2").pspName("psp1")))
        .compose(_ -> seeder.addMerchantToPsp(b -> b.merchantName("m3").pspName("psp2")))
        .compose(
            _ ->
                seeder.addMerchantToMerchantGroup(
                    b -> b.merchantNames(Set.of("m4")).merchantGroupName("mg1")))
        .compose(
            _ ->
                seeder.addMerchantToCustomMerchantGroup(
                    b -> b.merchantNames(Set.of("m5")).customMerchantGroupName("cmg1")))
        // scopes
        .compose(
            _ -> seeder.addUserToPspScope(b -> b.userName("u1").pspNames(Set.of("psp1", "psp2"))))
        .compose(
            _ -> seeder.addUserToMerchantScope(b -> b.userName("u1").merchantNames(Set.of("m6"))))
        .compose(
            _ ->
                seeder.addUserToMerchantGroupScope(
                    b -> b.userName("u1").merchantGroupNames(Set.of("mg1"))))
        .compose(
            _ ->
                seeder.addUserToCustomMerchantGroupScope(
                    b -> b.userName("u1").customMerchantGroupNames(Set.of("cmg1"))));
  }

  private Future<Void> permissionPreconditions() {
    return seeder
        .createApplication(b -> b.name("a1"))
        .compose(_ -> seeder.createApplication(b -> b.name("a2")))
        .compose(
            _ ->
                seeder.createPermission(
                    b -> b.applicationName("a1").permissions(Set.of("a1:p1", "a1:p2"))))
        .compose(
            _ -> seeder.createPermission(b -> b.applicationName("a2").permissions(Set.of("a2:p1"))))
        .compose(_ -> seeder.createRole(b -> b.name("r1")))
        .compose(_ -> seeder.createRole(b -> b.name("r2")))
        .compose(
            _ ->
                seeder.assignRolePermissions(
                    b -> b.roleName("r1").permissionValues(Set.of("a1:p1"))))
        .compose(
            _ ->
                seeder.assignRolePermissions(
                    b -> b.roleName("r2").permissionValues(Set.of("a1:p1", "a2:p1"))))
        .compose(_ -> seeder.createUser(b -> b.name("u1")))
        .compose(_ -> seeder.createUser(b -> b.name("u2")))
        .compose(_ -> seeder.assignUserRole(b -> b.userName("u1").roleNames(Set.of("r1"))))
        .compose(_ -> seeder.assignUserRole(b -> b.userName("u2").roleNames(Set.of("r1", "r2"))));
  }
}
