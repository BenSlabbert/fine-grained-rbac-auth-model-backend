/* Licensed under Apache-2.0 2026. */
package org.example.iam.seeder;

import static org.example.iam.PostgresTestBase.ADMIN_AUTH;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.function.Consumer;
import org.example.iam.web.route.*;

public class ApiSeeder {

  private final WebClient wc;

  public ApiSeeder(Vertx vertx, int port) {
    var opts = new WebClientOptions();
    opts.setDefaultHost("127.0.0.1");
    opts.setDefaultPort(port);
    this.wc = WebClient.create(vertx, opts);
  }

  private static Void voidFunction(Object o) {
    return null;
  }

  public Future<Void> createApplication(Consumer<CreateApplicationRequestBuilder.Builder> c) {
    var b = CreateApplicationRequestBuilder.builder();
    c.accept(b);
    var payload = b.build();
    return wc.put("/application")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateApplicationRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createUser(Consumer<CreateUserRequestBuilder.Builder> c) {
    var builder = CreateUserRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
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
    var builder = CreateRoleRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/role")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateRoleRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createPermission(Consumer<AddPermissionsRequestBuilder.Builder> c) {
    var builder = AddPermissionsRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/application/permissions")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddPermissionsRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> assignRolePermissions(
      Consumer<AssignRolePermissionsRequestBuilder.Builder> c) {
    var builder = AssignRolePermissionsRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/role/assign")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AssignRolePermissionsRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> assignUserRole(Consumer<AssignUserRoleRequestBuilder.Builder> c) {
    var builder = AssignUserRoleRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/role/assign/user")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AssignUserRoleRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createPsp(Consumer<CreatePspRequestBuilder.Builder> c) {
    var builder = CreatePspRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.put("/psp")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreatePspRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createMerchant(Consumer<CreateMerchantRequestBuilder.Builder> c) {
    var builder = CreateMerchantRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.put("/merchant")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateMerchantRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> addMerchantToPsp(Consumer<AddMerchantToPspRequestBuilder.Builder> c) {
    var builder = AddMerchantToPspRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/merchant/psp")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddMerchantToPspRequestJson.toJson(payload))
        .expecting(r -> 200 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createCustomMerchantGroup(
      Consumer<CreateCustomMerchantGroupBuilder.Builder> c) {
    CreateCustomMerchantGroupBuilder.Builder builder = CreateCustomMerchantGroupBuilder.builder();
    c.accept(builder);
    CreateCustomMerchantGroup payload = builder.build();
    return wc.put("/merchant/custom-merchant-group")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateCustomMerchantGroupJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> addMerchantToCustomMerchantGroup(
      Consumer<AddMerchantToCustomMerchantGroupRequestBuilder.Builder> c) {
    var builder = AddMerchantToCustomMerchantGroupRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/merchant/custom-merchant-group/add")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddMerchantToCustomMerchantGroupRequestJson.toJson(payload))
        .expecting(r -> 200 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> createMerchantGroup(Consumer<CreateMerchantGroupRequestBuilder.Builder> c) {
    var builder = CreateMerchantGroupRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.put("/merchant-group")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(CreateMerchantGroupRequestJson.toJson(payload))
        .expecting(r -> 201 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> addMerchantToMerchantGroup(
      Consumer<AddMerchantToMerchantGroupRequestBuilder.Builder> c) {
    var builder = AddMerchantToMerchantGroupRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/merchant-group/add")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddMerchantToMerchantGroupRequestJson.toJson(payload))
        .expecting(r -> 200 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> addUserToPspScope(Consumer<AddUserToPspScopeRequestBuilder.Builder> c) {
    var builder = AddUserToPspScopeRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/scope/user/psp")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddUserToPspScopeRequestJson.toJson(payload))
        .expecting(r -> 200 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> addUserToMerchantScope(
      Consumer<AddUserToMerchantScopeRequestBuilder.Builder> c) {
    var builder = AddUserToMerchantScopeRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/scope/user/merchant")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddUserToMerchantScopeRequestJson.toJson(payload))
        .expecting(r -> 200 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> addUserToMerchantGroupScope(
      Consumer<AddUserToMerchantGroupScopeRequestBuilder.Builder> c) {
    var builder = AddUserToMerchantGroupScopeRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/scope/user/merchant-group")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddUserToMerchantGroupScopeRequestJson.toJson(payload))
        .expecting(r -> 200 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<Void> addUserToCustomMerchantGroupScope(
      Consumer<AddUserToCustomMerchantGroupScopeRequestBuilder.Builder> c) {
    var builder = AddUserToCustomMerchantGroupScopeRequestBuilder.builder();
    c.accept(builder);
    var payload = builder.build();
    return wc.post("/scope/user/custom-merchant-group")
        .authentication(ADMIN_AUTH)
        .sendJsonObject(AddUserToCustomMerchantGroupScopeRequestJson.toJson(payload))
        .expecting(r -> 200 == r.statusCode())
        .map(ApiSeeder::voidFunction);
  }

  public Future<HasPermissionResponse> userHasPspScope(String user, String psp) {
    return wc.get("/application/scope/psp/%s/%s".formatted(user, psp))
        .authentication(ADMIN_AUTH)
        .send()
        .expecting(r -> 200 == r.statusCode())
        .map(r -> HasPermissionResponseJson.fromJson(r.bodyAsJsonObject()));
  }

  public Future<HasPermissionResponse> userHasMerchantScope(String user, String merchant) {
    return wc.get("/application/scope/merchant/%s/%s".formatted(user, merchant))
        .authentication(ADMIN_AUTH)
        .send()
        .expecting(r -> 200 == r.statusCode())
        .map(r -> HasPermissionResponseJson.fromJson(r.bodyAsJsonObject()));
  }

  public Future<HasPermissionResponse> userHasMerchantGroupScope(
      String user, String merchantGroup) {
    return wc.get("/application/scope/merchant-group/%s/%s".formatted(user, merchantGroup))
        .authentication(ADMIN_AUTH)
        .send()
        .expecting(r -> 200 == r.statusCode())
        .map(r -> HasPermissionResponseJson.fromJson(r.bodyAsJsonObject()));
  }
}
