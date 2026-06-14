/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.HasRole;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.UserContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebHandler(path = "/ping")
class Ping {

  private static final Logger log = LoggerFactory.getLogger(Ping.class);

  @Inject
  Ping() {}

  @Get
  @HasRole("admin")
  void ping(@WebRequest.RoutingContext RoutingContext ctx) {
    // the user is here, it is added by
    User user = ctx.user();
    UserContext userContext = ctx.userContext();
    Session session = ctx.session();
    log.debug("Ping - pong");
    ctx.response().end("pong");
  }
}
