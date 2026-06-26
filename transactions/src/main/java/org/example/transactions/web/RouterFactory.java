/* Licensed under Apache-2.0 2024. */
package org.example.transactions.web;

import github.benslabbert.vdw.codegen.commons.RouterConfigurer;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RouterFactory {

  private static final Logger log = LoggerFactory.getLogger(RouterFactory.class);

  private final Set<RouterConfigurer> routerConfigurers;
  private final JWTAuth jwtAuth;
  private final Vertx vertx;

  @Inject
  RouterFactory(Set<RouterConfigurer> routerConfigurers, JWTAuth jwtAuth, Vertx vertx) {
    this.routerConfigurers = routerConfigurers;
    this.jwtAuth = jwtAuth;
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
        .handler(CorsHandler.create())
        .handler(BodyHandler.create().setBodyLimit(1024L * 100L))
        // this is applied to all handlers/routes
        .handler(JWTAuthHandler.create(jwtAuth));

    routerConfigurers.forEach(rc -> rc.route(router));
    return router;
  }
}
