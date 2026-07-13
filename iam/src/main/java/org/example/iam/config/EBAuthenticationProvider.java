/* Licensed under Apache-2.0 2026. */
package org.example.iam.config;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.serviceproxy.AuthenticationInterceptor;
import io.vertx.serviceproxy.AuthorizationInterceptor;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Module
class EBAuthenticationProvider {

  @Provides
  @Singleton
  static AuthenticationInterceptor authenticationInterceptor(
      @Named("machine-jwt") JWTAuth machineJwtAuth) {
    return AuthenticationInterceptor.create(
        credentials ->
            switch (credentials) {
              case TokenCredentials tc -> machineJwtAuth.authenticate(tc);
              case null -> throw new IllegalStateException("cannot be null");
              default -> throw new IllegalStateException("Unexpected value: " + credentials);
            });
  }

  @Provides
  @Singleton
  static AuthorizationInterceptor authorizationInterceptor() {
    return AuthorizationInterceptor.create(
        new AuthorizationProvider() {
          @Override
          public String getId() {
            return "custom-id";
          }

          @Override
          public Future<Void> getAuthorizations(User user) {
            user.authorizations().put(getId(), RoleBasedAuthorization.create("system"));
            return Future.succeededFuture();
          }
        });
  }
}
