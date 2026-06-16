/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Post;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.UserContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/login")
class LoginHandler {

  private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

  @Inject
  LoginHandler() {}

  @Post
  void login(@WebRequest.RoutingContext RoutingContext ctx) {
    Session session = ctx.session();
    User user = ctx.user();
    UserContext userContext = ctx.userContext();
    log.info("login");
    ctx.end();
  }
}
