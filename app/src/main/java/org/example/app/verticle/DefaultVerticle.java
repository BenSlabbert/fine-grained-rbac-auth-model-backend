/* Licensed under Apache-2.0 2026. */
package org.example.app.verticle;

import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.util.Objects;
import org.example.app.di.DaggerProvider;
import org.example.app.di.Provider;
import org.example.app.web.RouterFactory;
import org.example.app.web.ServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(DefaultVerticle.class);

  private volatile HttpServer httpServer = null;

  private void setHttpServer(HttpServer httpServer) {
    this.httpServer = httpServer;
  }

  public int getPort() {
    Objects.requireNonNull(httpServer, "httpServer is null");
    return httpServer.actualPort();
  }

  @Override
  public void start(Promise<Void> startPromise) {
    log.info("Starting verticle");
    vertx.exceptionHandler(throwable -> log.error("unhandled exception", throwable));
    ApplicationConfig applicationConfig = ApplicationConfig.fromJson(config());
    Provider provider =
        DaggerProvider.builder().vertx(vertx).appConfig(applicationConfig).config(config()).build();
    provider.init();

    ServerFactory serverFactory = provider.serverFactory();
    RouterFactory routerFactory = provider.routerFactory();
    Router router = routerFactory.createRouter();

    serverFactory
        .create(router)
        .listen()
        .onComplete(
            res -> {
              if (res.succeeded()) {
                log.info("listening for requests on port: {}", res.result().actualPort());
                setHttpServer(res.result());
                startPromise.complete();
              } else {
                startPromise.fail(res.cause());
              }
            });
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    log.info("Stopping verticle");

    if (null == httpServer) {
      stopPromise.complete();
      return;
    }

    log.info("Stopping http server");
    httpServer
        .close()
        .onComplete(
            e -> {
              if (e.succeeded()) {
                log.info("http server closed");
                stopPromise.complete();
              } else {
                log.error("stopping http server failed", e.cause());
                stopPromise.fail(e.cause());
              }
            });
  }
}
