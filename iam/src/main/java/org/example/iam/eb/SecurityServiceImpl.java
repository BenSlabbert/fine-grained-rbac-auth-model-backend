/* Licensed under Apache-2.0 2026. */
package org.example.iam.eb;

import io.vertx.core.Future;
import javax.inject.Inject;
import org.example.iam.service.AuthService;
import org.example.security.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SecurityServiceImpl implements SecurityService {

  private static final Logger log = LoggerFactory.getLogger(SecurityServiceImpl.class);

  private final AuthService authService;

  @Inject
  SecurityServiceImpl(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public Future<HasPermissionResponse> hasPermission(HasPermissionRequest r) {
    log.info("hasPermission {}", r);
    var has = authService.hasPermission(r.application(), r.user(), r.permission());
    var resp = HasPermissionResponseBuilder.builder().hasPermission(has).build();
    return Future.succeededFuture(resp);
  }

  @Override
  public Future<ApplicationUserPermissionsResponse> getApplicationUserPermissions(
      ApplicationUserPermissionsRequest r) {
    log.info("getApplicationUserPermissions {}", r);
    var has = authService.getApplicationUserPermissions(r.application(), r.user());
    var resp = ApplicationUserPermissionsResponseBuilder.builder().permissions(has).build();
    return Future.succeededFuture(resp);
  }

  @Override
  public Future<UserPspScopeResponse> userHasPspScope(UserPspScopeRequest r) {
    log.info("userHasPspScope {}", r);
    var has = authService.userHasPspScope(r.user(), r.psp());
    var resp = UserPspScopeResponseBuilder.builder().hasPermission(has).build();
    return Future.succeededFuture(resp);
  }

  @Override
  public Future<UserMerchantScopeResponse> userHasMerchantScope(UserMerchantScopeRequest r) {
    log.info("userHasMerchantScope {}", r);
    var has = authService.userHasMerchantScope(r.user(), r.merchant());
    var resp = UserMerchantScopeResponseBuilder.builder().hasPermission(has).build();
    return Future.succeededFuture(resp);
  }

  @Override
  public Future<UserMerchantGroupScopeResponse> userHasMerchantGroupScope(
      UserMerchantGroupScopeRequest r) {
    log.info("userHasMerchantGroupScope {}", r);
    var has = authService.userHasMerchantGroupScope(r.user(), r.merchantGroup());
    var resp = UserMerchantGroupScopeResponseBuilder.builder().hasPermission(has).build();
    return Future.succeededFuture(resp);
  }
}
