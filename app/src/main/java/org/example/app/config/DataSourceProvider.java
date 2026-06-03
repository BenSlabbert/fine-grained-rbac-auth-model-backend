/* Licensed under Apache-2.0 2026. */
package org.example.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dagger.Module;
import dagger.Provides;
import github.benslabbert.vdw.codegen.config.ApplicationConfig;
import jakarta.inject.Singleton;
import java.util.Objects;
import javax.sql.DataSource;

@Module
final class DataSourceProvider {

  private DataSourceProvider() {}

  @Singleton
  @Provides
  static DataSource dataSource(ApplicationConfig applicationConfig) {
    HikariConfig hikariConfig = new HikariConfig();
    ApplicationConfig.PostgresConfig postgresConfig = applicationConfig.postgresConfig();
    Objects.requireNonNull(postgresConfig, "postgresConfig is null");

    hikariConfig.setJdbcUrl(postgresConfig.uri());
    hikariConfig.setUsername(postgresConfig.username());
    hikariConfig.setPassword(postgresConfig.password());
    hikariConfig.setAutoCommit(false);
    hikariConfig.setMaximumPoolSize(2);
    hikariConfig.setPoolName("jdbc");
    hikariConfig.setThreadFactory(Thread.ofVirtual().name("v-", 0L).factory());

    return new HikariDataSource(hikariConfig);
  }
}
