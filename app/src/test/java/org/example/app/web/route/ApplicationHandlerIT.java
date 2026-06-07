/* Licensed under Apache-2.0 2026. */
package org.example.app.web.route;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Future;
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
    preconditions()
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
    preconditions()
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

  private Future<Void> preconditions() {
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
