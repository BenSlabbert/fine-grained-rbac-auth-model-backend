/* Licensed under Apache-2.0 2024. */
package org.example.app.web;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.Router;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ServerFactory {

  private final Vertx vertx;
  private final JsonObject config;

  @Inject
  ServerFactory(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
  }

  public HttpServer create(Router router) {
    return vertx
        .createHttpServer(
            new HttpServerOptions()
                .setTracingPolicy(TracingPolicy.ALWAYS)
                .setPort(config.getInteger("http.port", 8080))
                .setHost("0.0.0.0"))
        .requestHandler(router);
  }
}
