/* Licensed under Apache-2.0 2026. */
package org.example.iam;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.benslabbert.vdw.codegen.commons.test.ConfigEncoder;
import github.benslabbert.vdw.codegen.commons.test.DockerContainers;
import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import github.benslabbert.vdw.codegen.config.ApplicationConfigBuilder;
import github.benslabbert.vdw.codegen.config.ApplicationConfig_HttpConfigBuilder;
import github.benslabbert.vdw.codegen.config.ApplicationConfig_PostgresConfigBuilder;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.TimeUnit;
import org.example.iam.verticle.DefaultVerticle;
import org.example.utilities.FlywayUtility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
@ExtendWith(VertxExtension.class)
public abstract class PostgresTestBase {

  private static final Logger log = LoggerFactory.getLogger(PostgresTestBase.class);

  public static final PostgreSQLContainer POSTGRES = DockerContainers.POSTGRES;
  public static final Credentials ADMIN_AUTH =
      new UsernamePasswordCredentials("name", "password").applyHttpChallenge(null);

  @BeforeAll
  static void start() {
    log.info("starting postgres...");
    POSTGRES.start();
    log.info("starting postgres...done");
  }

  @AfterAll
  static void stop() {
    log.info("stopping postgres...");
    POSTGRES.stop();
    log.info("stopping postgres...done");
  }

  private volatile DefaultVerticle verticle;

  private static HikariDataSource getDatasource() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(POSTGRES.getJdbcUrl());
    hikariConfig.setUsername(POSTGRES.getUsername());
    hikariConfig.setPassword(POSTGRES.getPassword());
    hikariConfig.setAutoCommit(false);
    hikariConfig.setMaximumPoolSize(2);
    hikariConfig.setPoolName("test");
    hikariConfig.setThreadFactory(Thread.ofVirtual().name("v-", 0L).factory());

    return new HikariDataSource(hikariConfig);
  }

  @BeforeEach
  void init(Vertx v, VertxTestContext tc) {
    ApplicationConfig applicationConfig =
        ApplicationConfigBuilder.builder()
            .postgresConfig(
                ApplicationConfig_PostgresConfigBuilder.builder()
                    .database(POSTGRES.getDatabaseName())
                    .host(POSTGRES.getHost())
                    .password(POSTGRES.getPassword())
                    .username(POSTGRES.getUsername())
                    .schema("public")
                    .port(POSTGRES.getMappedPort(5432))
                    .build())
            .httpConfig(ApplicationConfig_HttpConfigBuilder.builder().port(0).build())
            .profile(ApplicationConfig.Profile.DEV)
            .build();

    verticle = new DefaultVerticle();
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

  @AfterEach
  void after() {
    try (var ds = getDatasource()) {
      FlywayUtility.clean(ds);
    }
  }
}
