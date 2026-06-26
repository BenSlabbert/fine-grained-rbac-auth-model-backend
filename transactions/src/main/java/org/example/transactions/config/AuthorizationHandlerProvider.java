/* Licensed under Apache-2.0 2024. */
package org.example.transactions.config;

import github.benslabbert.vdw.codegen.commons.RoleAuthorizationHandlerProvider;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.OrAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.web.handler.AuthorizationHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Set;
import java.util.stream.Collectors;
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
    return TransactionsConfig.APP_NAME;
  }

  @Override
  public Future<Void> getAuthorizations(User user) {
    if (user.authorizations().contains(getId())) {
      return Future.succeededFuture();
    }

    return securityService
        .getApplicationUserPermissions(
            ApplicationUserPermissionsRequestBuilder.builder()
                .application(TransactionsConfig.APP_NAME)
                .user(user.subject())
                .build())
        .map(
            r -> {
              Set<Authorization> roles =
                  r.permissions().stream()
                      .map(RoleBasedAuthorization::create)
                      .collect(Collectors.toSet());
              user.authorizations().put(getId(), roles);
              return null;
            });
  }
}
