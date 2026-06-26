/* Licensed under Apache-2.0 2026. */
package org.example.transactions.verticle;

import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.util.Objects;
import org.example.security.api.SecurityServiceVertxEBClientProxy;
import org.example.transactions.config.TransactionsConfig;
import org.example.transactions.di.DaggerProvider;
import org.example.transactions.di.Provider;
import org.example.transactions.web.RouterFactory;
import org.example.transactions.web.ServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(DefaultVerticle.class);

  private volatile HttpServer httpServer = null;
  private volatile Provider provider = null;

  private final TransactionsConfig transactionsConfig;

  // required for vert.x
  public DefaultVerticle() {
    this(null);
  }

  public DefaultVerticle(TransactionsConfig transactionsConfig) {
    this.transactionsConfig = transactionsConfig;
  }

  @Override
  public void init(Vertx vertx, Context context) {
    log.info("init verticle");
    super.init(vertx, context);

    provider =
        DaggerProvider.builder()
            .vertx(vertx)
            .appConfig(ApplicationConfig.fromJson(config()))
            .transactionsConfig(
                null == transactionsConfig
                    ? TransactionsConfig.create(config().getJsonObject(TransactionsConfig.APP_NAME))
                    : transactionsConfig)
            .securityService(new SecurityServiceVertxEBClientProxy(vertx))
            .config(config())
            .build();
  }

  public int getPort() {
    Objects.requireNonNull(httpServer, "httpServer is null");
    return httpServer.actualPort();
  }

  public Provider getProvider() {
    Objects.requireNonNull(provider, "provider is null");
    return provider;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    log.info("Starting verticle");
    vertx.exceptionHandler(throwable -> log.error("unhandled exception", throwable));
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
                this.httpServer = res.result();
                startPromise.complete();
              } else {
                startPromise.fail(res.cause());
              }
            });
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    log.info("Stopping verticle");

    log.info("Stopping ProxyHandlers");

    if (null == httpServer) {
      stopPromise.complete();
      return;
    }

    log.info("Stopping http server");
    httpServer
        .close()
        .onComplete(
            e -> {
              provider.closeSilently();

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
