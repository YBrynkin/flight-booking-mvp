package ru.brynkin;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import ru.brynkin.config.DatabaseConfig;
import ru.brynkin.config.FlywayConfig;
import ru.brynkin.util.DatabaseMigrator;

/**
 * This is JavaDoc for Main class. Just build it, run it and enjoy the trip!
 */
public class Main {
  public static void main(String[] args) {

    // 1. Initialize data source
    DataSource dataSource = DatabaseConfig.getDataSource();

    // 2. Configure and run migrations
    Flyway flyway = new FlywayConfig(dataSource).flyway();
    DatabaseMigrator.runMigrations(flyway);
  }

}