package ru.brynkin.flightbooking.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import ru.brynkin.flightbooking.util.PropertiesUtil;

/**
 * Hikari Connection pool configuration class
 */

public class DatabaseConfig {

  private static final String DB_URL = "db.url";
  private static final String DB_USER = "db.user";
  private static final String DB_PASSWORD = "db.password";
  private static final String DB_DRIVER = "db.driver";
  private static final String DB_MAX_POOL_SIZE = "db.maximum-pool-size";
  private static final String DB_CONNECTION_TIMEOUT = "db.connection-timeout";
  private static final String DB_MIN_IDLE = "db.minimum-idle";
  private static final String DB_IDLE_TIMEOUT = "db.idle-timeout";
  private static final String DB_MAX_LIFETIME = "db.max-lifetime";

  private static final HikariDataSource dataSource;

  static {
    // Basic configuration
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(PropertiesUtil.get(DB_URL));
    config.setUsername(PropertiesUtil.get(DB_USER));
    config.setPassword(PropertiesUtil.get(DB_PASSWORD));
    config.setDriverClassName(PropertiesUtil.get(DB_DRIVER));

    // Connection pool settings
    config.setMaximumPoolSize(Integer.parseInt(
        PropertiesUtil.get(DB_MAX_POOL_SIZE)));          // Maximum connections in pool
    config.setMinimumIdle(Integer.parseInt(
        PropertiesUtil.get(DB_MIN_IDLE)));               // Minimum idle connections
    config.setConnectionTimeout(Long.parseLong(
        PropertiesUtil.get(DB_CONNECTION_TIMEOUT)));     // 10 seconds to acquire connection
    config.setIdleTimeout(
        Long.parseLong(PropertiesUtil.get(DB_IDLE_TIMEOUT)));          // 10 minutes idle timeout
    config.setMaxLifetime(Long.parseLong(
        PropertiesUtil.get(DB_MAX_LIFETIME)));         // 30 minutes max connection lifetime
    config.setPoolName("FlightBookingCP");

    // PostgreSQL-specific optimizations
    config.addDataSourceProperty("preparedStatementCacheQueries", 256);
    config.addDataSourceProperty("preparedStatementCacheSizeMiB", 5);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("useServerPrepStmts", "true");

    dataSource = new HikariDataSource(config);
    System.out.println("HikariCP pool initialized successfully for database" +
                       PropertiesUtil.get(DB_URL));
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
