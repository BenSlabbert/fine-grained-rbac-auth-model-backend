/* Licensed under Apache-2.0 2024. */
package org.example.gateway.config;

import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
class AuthenticationHandlerProvider implements AuthenticationProvider {

  @Inject
  AuthenticationHandlerProvider() {}

  @Override
  public Future<User> authenticate(Credentials credentials) {
    if (credentials instanceof UsernamePasswordCredentials c) {
      c.checkValid(null);
      if ("name".equals(c.getUsername()) && "password".equals(c.getPassword())) {
        return Future.succeededFuture(User.fromName(c.getUsername()));
      }
    }
    return Future.failedFuture("Invalid credentials");
  }
}
