package ru.brynkin.flightbooking.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import ru.brynkin.flightbooking.util.PropertiesUtil;

/**
 * Flyway configuration class
 */

public class FlywayConfig {

  private static final String FLYWAY_LOCATION = "flyway.locations";
  private static final String FLYWAY_BASELINE_ON_MIGRATE = "flyway.baseline-on-migrate";
  private static final String FLYWAY_VALIDATE_ON_MIGRATE = "flyway.validate-on-migrate";
  private static final String FLYWAY_CLEAN_DISABLED = "flyway.clean-disabled";
  private static final String FLYWAY_OUT_OF_ORDER = "flyway.out-of-order";


  private final DataSource dataSource;

  public FlywayConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Flyway flyway() {
    return Flyway.configure()
        .dataSource(dataSource)
        .locations(PropertiesUtil.get(FLYWAY_LOCATION))
        .baselineOnMigrate(Boolean.parseBoolean(PropertiesUtil.get(FLYWAY_BASELINE_ON_MIGRATE)))
        .validateOnMigrate(Boolean.parseBoolean(PropertiesUtil.get(FLYWAY_VALIDATE_ON_MIGRATE)))
        .cleanDisabled(Boolean.parseBoolean(
            PropertiesUtil.get(FLYWAY_CLEAN_DISABLED)))  // Enable clean for dev (disable in prod)
        .outOfOrder(Boolean.parseBoolean(
            PropertiesUtil.get(FLYWAY_OUT_OF_ORDER)))      // Allow out-of-order migrations
        .load();
  }
}
