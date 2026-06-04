/* Licensed under Apache-2.0 2026. */
package org.example.utilities;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.output.CleanResult;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FlywayUtility {

  private static final Logger log = LoggerFactory.getLogger(FlywayUtility.class);

  private FlywayUtility() {}

  public static void migrate(DataSource dataSource) {
    Flyway flyway = getFlyway(dataSource).load();

    MigrateResult result = flyway.migrate();
    if (result.success) {
      log.info(
          "flyway migration complete: {} migrations applied in {}ms",
          result.migrationsExecuted,
          result.getTotalMigrationTime());
    } else {
      throw new IllegalStateException("flyway migration failed");
    }
  }

  public static void clean(DataSource dataSource) {
    Flyway flyway = getFlyway(dataSource).cleanDisabled(false).load();

    CleanResult result = flyway.clean();
    log.info(
        "flyway clean complete database{} operation {} schemasCleaned {} schemasDropped {}",
        result.database,
        result.operation,
        result.schemasCleaned,
        result.schemasDropped);
  }

  private static FluentConfiguration getFlyway(DataSource dataSource) {
    return Flyway.configure()
        .dataSource(dataSource)
        .validateMigrationNaming(true)
        .validateOnMigrate(true)
        .failOnMissingLocations(true)
        .group(false);
  }
}
