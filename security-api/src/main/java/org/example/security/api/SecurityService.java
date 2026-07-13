/* Licensed under Apache-2.0 2026. */
package org.example.security.api;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.eb.EventBusService;
import io.vertx.core.Future;

@EventBusService(address = "iam")
public interface SecurityService {

  @HasRole("system")
  Future<HasPermissionResponse> hasPermission(HasPermissionRequest request);

  @HasRole("system")
  Future<ApplicationUserPermissionsResponse> getApplicationUserPermissions(
      ApplicationUserPermissionsRequest request);

  @HasRole("system")
  Future<UserPspScopeResponse> userHasPspScope(UserPspScopeRequest request);

  @HasRole("system")
  Future<UserMerchantScopeResponse> userHasMerchantScope(UserMerchantScopeRequest request);

  @HasRole("system")
  Future<UserMerchantGroupScopeResponse> userHasMerchantGroupScope(
      UserMerchantGroupScopeRequest request);
}
