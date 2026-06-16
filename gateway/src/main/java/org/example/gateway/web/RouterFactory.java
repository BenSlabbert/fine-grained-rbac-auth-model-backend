/* Licensed under Apache-2.0 2024. */
package org.example.gateway.web;

import github.benslabbert.vdw.codegen.commons.RouterConfigurer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
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
        .handler(BodyHandler.create().setBodyLimit(1024L * 100L));

    router
        .route(HttpMethod.POST, "/login")
        .handler(
            ctx -> {
              BasicAuthHandler.create(authenticationProvider).handle(ctx);
            });

    routerConfigurers.forEach(rc -> rc.route(router));

    return router;
  }
}
