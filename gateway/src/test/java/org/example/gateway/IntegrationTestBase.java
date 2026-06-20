/* Licensed under Apache-2.0 2026. */
package org.example.gateway;

import github.benslabbert.vdw.codegen.commons.test.ConfigEncoder;
import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import github.benslabbert.vdw.codegen.config.ApplicationConfigBuilder;
import github.benslabbert.vdw.codegen.config.ApplicationConfig_HttpConfigBuilder;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.example.gateway.config.GatewayConfig;
import org.example.gateway.config.GatewayConfigBuilder;
import org.example.gateway.config.GatewayConfig_JwtBuilder;
import org.example.gateway.verticle.DefaultVerticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
@ExtendWith(VertxExtension.class)
public abstract class IntegrationTestBase {

  private static final Logger log = LoggerFactory.getLogger(IntegrationTestBase.class);

  public static final Credentials ADMIN_AUTH =
      new UsernamePasswordCredentials("name", "password").applyHttpChallenge(null);

  private volatile DefaultVerticle verticle;

  /// Override this method if your test needs to customize the verticle.
  /// Be sure to call [IntegrationTestBase#deployVerticle] with the adjusted config.
  @BeforeEach
  protected void init(Vertx v, VertxTestContext tc) {
    GatewayConfig gatewayConfig =
        GatewayConfigBuilder.builder()
            .services(List.of())
            .jwt(GatewayConfig_JwtBuilder.builder().secret(Buffer.buffer("secret")).build())
            .build();
    deployVerticle(v, tc, gatewayConfig);
  }

  protected void deployVerticle(Vertx v, VertxTestContext tc, GatewayConfig gatewayConfig) {
    ApplicationConfig applicationConfig =
        ApplicationConfigBuilder.builder()
            .httpConfig(ApplicationConfig_HttpConfigBuilder.builder().port(0).build())
            .profile(ApplicationConfig.Profile.DEV)
            .build();
    verticle = new DefaultVerticle(gatewayConfig);
    long start = System.currentTimeMillis();
    v.deployVerticle(
            verticle,
            new DeploymentOptions()
                .setConfig(ConfigEncoder.encode(applicationConfig))
                .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                .setHa(false)
                .setInstances(1)
                .setWorkerPoolSize(1))
        .andThen(
            _ -> {
              long time = System.currentTimeMillis() - start;
              log.info("deploy time {}ms", time);
            })
        .onComplete(tc.succeedingThenComplete());
  }

  protected int getPort() {
    return verticle.getPort();
  }

  protected WebClient getWebClient(Vertx vertx) {
    WebClientOptions webClientOptions = new WebClientOptions();
    webClientOptions.setDefaultHost("127.0.0.1");
    webClientOptions.setDefaultPort(getPort());
    return WebClient.create(vertx, webClientOptions);
  }
}
