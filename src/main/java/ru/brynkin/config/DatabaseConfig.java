package ru.brynkin.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 * Hikari Connection pool configuration class
 */

public class DatabaseConfig {
  private static final HikariDataSource dataSource;

  static {
    // Basic configuration
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:postgresql://localhost:5433/flight_booking");
    config.setUsername("flight_booking_admin");
    config.setPassword("rules");
    config.setDriverClassName("org.postgresql.Driver");

    // Connection pool settings
    config.setMaximumPoolSize(10);          // Maximum connections in pool
    config.setMinimumIdle(5);               // Minimum idle connections
    config.setConnectionTimeout(10000);     // 10 seconds to acquire connection
    config.setIdleTimeout(600000);          // 10 minutes idle timeout
    config.setMaxLifetime(1800000);         // 30 minutes max connection lifetime
    config.setPoolName("FlightBookingCP");

    // PostgreSQL-specific optimizations
    config.addDataSourceProperty("preparedStatementCacheQueries", 256);
    config.addDataSourceProperty("preparedStatementCacheSizeMiB", 5);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("useServerPrepStmts", "true");

    dataSource = new HikariDataSource(config);
    System.out.println("HikariCP pool initialized successfully");
  }

  private DatabaseConfig() {

  }

  public static DataSource getDataSource() {
    return dataSource;
  }

  public static void closeDataSource() {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
      System.out.println("Database connection pool closed");
    }
  }


}
