package ru.brynkin;

import ru.brynkin.config.DatabaseConfig;
import ru.brynkin.config.FlywayConfig;
import ru.brynkin.util.ConnectionManager;

/**
 * This is JavaDoc for Main class. Just build it, run it and enjoy the trip!
 */
public class Main {
  public static void main(String[] args) {

    try {
      // Initialize database
      FlywayConfig.migrate();

      // Test connection
      ConnectionManager.testConnection();

      // Application startup
      System.out.println("Application started successfully");

    } catch (Exception e) {
      System.err.println("Application startup failed");
      e.printStackTrace();
      System.exit(1);
    } finally {
      DatabaseConfig.closeDataSource();
    }
  }
}