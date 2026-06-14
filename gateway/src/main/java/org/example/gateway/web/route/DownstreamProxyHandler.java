/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import github.benslabbert.vdw.codegen.annotation.web.WebHandler;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest;
import github.benslabbert.vdw.codegen.annotation.web.WebRequest.All;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;

@WebHandler(path = "/api/*")
class DownstreamProxyHandler {

  private final Vertx vertx;

  @Inject
  DownstreamProxyHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @All
  void all(@WebRequest.RoutingContext RoutingContext ctx) {
    User user = ctx.user();
    if (null == user) {
      // not authenticated
      ctx.response().setStatusCode(401).end();
      return;
    }
    String uri = ctx.request().uri();
    String context = uri.substring(0, uri.indexOf('/'));
    // find a downstream service for this context
    // generate a JWT token for this call
    // execute the call
    // send response back to the client
  }
}
