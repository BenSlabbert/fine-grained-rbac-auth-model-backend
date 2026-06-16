/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import io.vertx.ext.web.UserContext;
import jakarta.inject.Inject;

@NoAuthCheck
@WebHandler(path = "/logout")
class LogoutHandler {

  @Inject
  LogoutHandler() {}

  @Post
  void logout(@WebRequest.UserContext UserContext ctx) {
    ctx.logout();
  }
}
