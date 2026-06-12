/* Licensed under Apache-2.0 2026. */
package org.example.security.api;

import github.benslabbert.vdw.codegen.annotation.eb.EventBusService;
import io.vertx.core.Future;

@EventBusService(address = "iam")
public interface SecurityService {

  Future<HasPermissionResponse> hasPermission(HasPermissionRequest request);

  Future<ApplicationUserPermissionsResponse> getApplicationUserPermissions(
      ApplicationUserPermissionsRequest request);

  Future<UserPspScopeResponse> userHasPspScope(UserPspScopeRequest request);

  Future<UserMerchantScopeResponse> userHasMerchantScope(UserMerchantScopeRequest request);

  Future<UserMerchantGroupScopeResponse> userHasMerchantGroupScope(
      UserMerchantGroupScopeRequest request);
}
