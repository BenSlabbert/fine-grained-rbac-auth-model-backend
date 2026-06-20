/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.auth.NoAuthCheck;
import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.Get;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoAuthCheck
@WebHandler(path = "/ping")
class Ping {

  private static final Logger log = LoggerFactory.getLogger(Ping.class);

  @Inject
  Ping() {}

  @Get
  void ping(@WebRequest.RoutingContext RoutingContext ctx) {
    log.debug("Ping - pong");
    ctx.response().end("pong");
  }
}
