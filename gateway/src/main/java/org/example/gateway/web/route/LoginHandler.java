/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import jakarta.inject.Inject;

@WebHandler(path = "/login")
class LoginHandler {

  private final AuthenticationHandler authenticationHandler;

  @Inject
  LoginHandler(AuthenticationProvider authenticationProvider) {
    this.authenticationHandler = BasicAuthHandler.create(authenticationProvider);
  }

  @Post
  void login(@WebRequest.RoutingContext RoutingContext ctx) {
    authenticationHandler.handle(ctx);
  }
}
