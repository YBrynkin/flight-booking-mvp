package ru.brynkin.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

/**
 * Flyway configuration class
 */

public class FlywayConfig {
  private final DataSource dataSource;

  public FlywayConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Flyway flyway() {
    return Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .validateOnMigrate(true)
        .cleanDisabled(false)  // Enable clean for dev (disable in prod)
        .outOfOrder(true)      // Allow out-of-order migrations
        .load();
  }
}
