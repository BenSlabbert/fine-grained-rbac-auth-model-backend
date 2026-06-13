/* Licensed under Apache-2.0 2024. */
package org.example.transactions.config;

import github.benslabbert.vdw.codegen.commons.RoleAuthorizationHandlerProvider;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.OrAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.web.handler.AuthorizationHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.example.security.api.ApplicationUserPermissionsRequestBuilder;
import org.example.security.api.SecurityService;

@Singleton
class AuthorizationHandlerProvider
    implements RoleAuthorizationHandlerProvider, AuthorizationProvider {

  private final SecurityService securityService;

  @Inject
  AuthorizationHandlerProvider(SecurityService securityService) {
    this.securityService = securityService;
  }

  @Override
  public AuthorizationHandler forRole(String role) {
    return AuthorizationHandler.create(RoleBasedAuthorization.create(role))
        .addAuthorizationProvider(this);
  }

  @Override
  public AuthorizationHandler forRoles(String... roles) {
    OrAuthorization orAuthorization = OrAuthorization.create();
    for (String role : roles) {
      orAuthorization.addAuthorization(RoleBasedAuthorization.create(role));
    }
    return AuthorizationHandler.create(orAuthorization).addAuthorizationProvider(this);
  }

  @Override
  public String getId() {
    return "transactions";
  }

  @Override
  public Future<Void> getAuthorizations(User user) {
    if (user.authorizations().contains(getId())) {
      return Future.succeededFuture();
    }

    return securityService
        .getApplicationUserPermissions(
            ApplicationUserPermissionsRequestBuilder.builder()
                .application("transactions")
                .user(user.subject())
                .build())
        .flatMap(
            r -> {
              for (var p : r.permissions()) {
                user.authorizations().put(getId(), RoleBasedAuthorization.create(p));
              }
              return Future.succeededFuture();
            });
  }
}
