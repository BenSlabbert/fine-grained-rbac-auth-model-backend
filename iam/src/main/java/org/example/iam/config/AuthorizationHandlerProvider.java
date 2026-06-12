/* Licensed under Apache-2.0 2024. */
package org.example.iam.config;

import github.benslabbert.vdw.codegen.commons.RoleAuthorizationHandlerProvider;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.OrAuthorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.web.handler.AuthorizationHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class AuthorizationHandlerProvider
    implements RoleAuthorizationHandlerProvider, AuthorizationProvider {

  @Inject
  AuthorizationHandlerProvider() {}

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
    return "example";
  }

  @Override
  public Future<Void> getAuthorizations(User user) {
    if (user.authorizations().contains(getId())) {
      return Future.succeededFuture();
    }

    user.authorizations().put(getId(), RoleBasedAuthorization.create("admin"));
    return Future.succeededFuture();
  }
}
