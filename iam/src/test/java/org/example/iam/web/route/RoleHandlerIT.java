/* Licensed under Apache-2.0 2026. */
package org.example.iam.web.route;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.util.Set;
import org.example.iam.PostgresTestBase;
import org.example.iam.seeder.ApiSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleHandlerIT extends PostgresTestBase {

  private ApiSeeder seeder;

  @BeforeEach
  void init(Vertx v) {
    seeder = new ApiSeeder(v, getPort());
  }

  @Test
  void test(VertxTestContext tc) {
    seeder
        .createUser(b -> b.name("u1"))
        .compose(_ -> seeder.createUser(b -> b.name("u2")))
        .compose(_ -> seeder.createRole(b -> b.name("r1")))
        .compose(_ -> seeder.createRole(b -> b.name("r2")))
        .compose(_ -> seeder.createRole(b -> b.name("r3")))
        .compose(_ -> seeder.createApplication(b -> b.name("a1")))
        .compose(_ -> seeder.createApplication(b -> b.name("a2")))
        .compose(
            _ ->
                seeder.createPermission(
                    b -> b.applicationName("a1").permissions(Set.of("a1:p1", "a1:p2"))))
        .compose(
            _ -> seeder.createPermission(b -> b.applicationName("a2").permissions(Set.of("a2:p1"))))
        .compose(
            _ ->
                seeder.assignRolePermissions(
                    b -> b.roleName("r1").permissionValues(Set.of("a1:p1"))))
        .compose(
            _ ->
                seeder.assignRolePermissions(
                    b -> b.roleName("r2").permissionValues(Set.of("a1:p1", "a1:p2"))))
        .compose(
            _ ->
                seeder.assignRolePermissions(
                    b -> b.roleName("r3").permissionValues(Set.of("a1:p1", "a2:p1"))))
        .compose(
            _ -> seeder.assignUserRole(b -> b.userName("u1").roleNames(Set.of("r1", "r2", "r3"))))
        .compose(_ -> seeder.assignUserRole(b -> b.userName("u2").roleNames(Set.of("r1"))))
        .onComplete(tc.succeedingThenComplete());
  }
}
