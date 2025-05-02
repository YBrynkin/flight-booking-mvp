package ru.brynkin.flightbooking.util;

import java.sql.Connection;
import java.sql.SQLException;
import ru.brynkin.flightbooking.config.DatabaseConfig;

/**
 * Connection manager class for out flight booking system project
 */
public class ConnectionManager {
  private ConnectionManager() {
  }

  public static Connection getConnection() throws SQLException {
    return DatabaseConfig.getDataSource().getConnection();
  }

  public static void testConnection() {
    try (Connection connection = getConnection()) {
      if (connection != null && connection.isValid(1000)) {
        System.out.println("Connection is valid!");
      }
    } catch (SQLException e) {
      ExeptionHandler.handleException("Database connection failed!", e);
      throw new RuntimeException("Database connection test failed", e);
    }

  }
}
