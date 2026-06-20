/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.UserContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoAuthCheck
@WebHandler(path = "/logout")
class LogoutHandler {
  private static final Logger log = LoggerFactory.getLogger(LogoutHandler.class);

  @Inject
  LogoutHandler() {}

  @Post
  void logout(@WebRequest.User User user, @WebRequest.UserContext UserContext ctx) {
    log.info("logout {}", user.subject());
    ctx.logout();
  }
}
