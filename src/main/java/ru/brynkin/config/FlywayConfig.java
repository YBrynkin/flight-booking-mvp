package ru.brynkin.config;

import org.flywaydb.core.Flyway;
import ru.brynkin.util.ExeptionHandler;

/**
 * Flyway configuration class
 */

public class FlywayConfig {
  public static void migrate() {
    try {
      Flyway flyway = Flyway.configure()
          .dataSource(DatabaseConfig.getDataSource())
          .locations("classpath:db/migration")
          .baselineOnMigrate(true)
          .load();

      flyway.migrate();
      System.out.println("Flyway migration complete successfully!");
    } catch (Exception e) {
      ExeptionHandler.handleException("Flyway migration failed!", e);
      throw new RuntimeException("Flyway migration test failed", e);
    }

  }
}
