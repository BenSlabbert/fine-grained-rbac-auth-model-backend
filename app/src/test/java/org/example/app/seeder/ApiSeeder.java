/* Licensed under Apache-2.0 2026. */
package org.example.app.seeder;

import static org.example.app.PostgresTestBase.ADMIN_AUTH;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.function.Consumer;
import org.example.app.web.route.*;

public class ApiSeeder {

  private final WebClient wc;

  public ApiSeeder(Vertx vertx, int port) {
    WebClientOptions webClientOptions = new WebClientOptions();
    webClientOptions.setDefaultHost("127.0.0.1");
    webClientOptions.setDefaultPort(port);
    this.wc = WebClient.create(vertx, webClientOptions);
  }

  private static Void voidFunction(Object o) {
    return null;
  }

  public Future<Void> createApplication(Consumer<CreateApplicationRequestBuilder.Builder> c) {
    CreateApplicationRequestBuilder.Builder b = CreateApplicationRequestBuilder.builder();
    c.accept(b);
    CreateApplicationRequest payload = b.build();
    return wc.put("/application")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateApplicationRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createUser(Consumer<CreateUserRequestBuilder.Builder> c) {
    CreateUserRequestBuilder.Builder builder = CreateUserRequestBuilder.builder();
    c.accept(builder);
    CreateUserRequest payload = builder.build();
    return wc.post("/user")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateUserRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<GetPermissionsResponse> getApplicationPermissions(String application) {
    return wc.get("/application/permissions/" + application)
        .authentication(ADMIN_AUTH)
        .send()
        .expecting(r -> 200 == r.statusCode())
        .map(r -> GetPermissionsResponseJson.fromJson(r.bodyAsJsonObject()));
  }

  public Future<GetPermissionsResponse> getApplicationUserPermissions(
      String application, String user) {
    return wc.get("/application/permissions/" + application + "/" + user)
        .authentication(ADMIN_AUTH)
        .send()
        .expecting(r -> 200 == r.statusCode())
        .map(r -> GetPermissionsResponseJson.fromJson(r.bodyAsJsonObject()));
  }

  public Future<HasPermissionResponse> hasPermission(
      String application, String user, String permission) {
    return wc.get("/application/permissions/" + application + "/" + user + "/" + permission)
        .authentication(ADMIN_AUTH)
        .send()
        .expecting(r -> 200 == r.statusCode())
        .map(r -> HasPermissionResponseJson.fromJson(r.bodyAsJsonObject()));
  }

  public Future<Void> createRole(Consumer<CreateRoleRequestBuilder.Builder> c) {
    CreateRoleRequestBuilder.Builder builder = CreateRoleRequestBuilder.builder();
    c.accept(builder);
    CreateRoleRequest payload = builder.build();
    return wc.post("/role")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateRoleRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createPermission(Consumer<AddPermissionsRequestBuilder.Builder> c) {
    AddPermissionsRequestBuilder.Builder builder = AddPermissionsRequestBuilder.builder();
    c.accept(builder);
    AddPermissionsRequest payload = builder.build();
    return wc.post("/application/permissions")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddPermissionsRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> assignRolePermissions(
      Consumer<AssignRolePermissionsRequestBuilder.Builder> c) {
    AssignRolePermissionsRequestBuilder.Builder builder =
        AssignRolePermissionsRequestBuilder.builder();
    c.accept(builder);
    AssignRolePermissionsRequest payload = builder.build();
    return wc.post("/role/assign")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AssignRolePermissionsRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> assignUserRole(Consumer<AssignUserRoleRequestBuilder.Builder> c) {
    AssignUserRoleRequestBuilder.Builder builder = AssignUserRoleRequestBuilder.builder();
    c.accept(builder);
    AssignUserRoleRequest payload = builder.build();
    return wc.post("/role/assign/user")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AssignUserRoleRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }
}
