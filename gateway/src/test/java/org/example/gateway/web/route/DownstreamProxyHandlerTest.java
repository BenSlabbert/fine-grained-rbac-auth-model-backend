/* Licensed under Apache-2.0 2026. */
package org.example.gateway.web.route;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.junit5.VertxTestContext;
import org.example.gateway.IntegrationTestBase;
import org.junit.jupiter.api.Test;

class DownstreamProxyHandlerTest extends IntegrationTestBase {

  @Test
  void test(Vertx v, VertxTestContext tc) {
    var r = Router.router(v);
    r.route(HttpMethod.GET, "/p1").handler(ctx -> ctx.response().setStatusCode(200).end());
    var s =
        v.createHttpServer(new HttpServerOptions().setPort(0).setHost("0.0.0.0")).requestHandler(r);

    s.listen().onComplete(tc.succeedingThenComplete());

    // after server has started
    // call gateway and assert downstreams are invoked
    // ensure headers and body is passed through as well
  }
}
