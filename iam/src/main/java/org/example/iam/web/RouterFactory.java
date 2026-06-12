/* Licensed under Apache-2.0 2024. */
package org.example.iam.web;

import github.benslabbert.vdw.codegen.commons.RouterConfigurer;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Set;

@Singleton
public class RouterFactory {

  private final AuthenticationProvider authenticationProvider;
  private final Set<RouterConfigurer> routerConfigurers;
  private final SessionHandler sessionHandler;
  private final Vertx vertx;

  @Inject
  RouterFactory(
      AuthenticationProvider authenticationProvider,
      Set<RouterConfigurer> routerConfigurers,
      SessionHandler sessionHandler,
      Vertx vertx) {
    this.authenticationProvider = authenticationProvider;
    this.routerConfigurers = routerConfigurers;
    this.sessionHandler = sessionHandler;
    this.vertx = vertx;
  }

  public Router createRouter() {
    Router router = Router.router(vertx);
    router
        .route()
        .handler(ResponseContentTypeHandler.create())
        .handler(LoggerHandler.create(false, LoggerFormat.DEFAULT))
        .handler(TimeoutHandler.create())
        .handler(ResponseTimeHandler.create())
        .handler(sessionHandler)
        .handler(CorsHandler.create())
        .handler(BodyHandler.create().setBodyLimit(1024L * 100L))
        // this is applied to all handlers/routes
        .handler(BasicAuthHandler.create(authenticationProvider));

    routerConfigurers.forEach(rc -> rc.route(router));
    return router;
  }
}
