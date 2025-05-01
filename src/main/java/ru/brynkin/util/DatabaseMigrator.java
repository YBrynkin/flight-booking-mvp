package ru.brynkin.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

/**
 * Special migrator class for my flight booking db
 */

public class DatabaseMigrator {
  public static MigrateResult runMigrations(Flyway flyway) {
    return flyway.migrate();
  }

  public static void cleanDatabase(Flyway flyway) {
    flyway.clean();
  }

}
