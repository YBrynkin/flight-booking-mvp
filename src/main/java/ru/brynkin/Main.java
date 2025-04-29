package ru.brynkin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import ru.brynkin.config.DatabaseConfig;
import ru.brynkin.config.FlywayConfig;
import ru.brynkin.dao.impl.AirportDaoImpl;
import ru.brynkin.entity.Airport;
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
      //ConnectionManager.testConnection();

      // Test DAO realization
      AirportDaoImpl airportDaoImpl = AirportDaoImpl.getInstance();
      List<Airport> airports = airportDaoImpl.findAll();
      for (Airport airport : airports) {
        System.out.println(airport);
      }
      System.out.println("DAO completed successfully");

    } catch (Exception e) {
      System.err.println("Application startup failed");
      e.printStackTrace();
      System.exit(1);
    } finally {
      DatabaseConfig.closeDataSource();
    }
  }

  private static void testSampleQueries() throws SQLException {
    try (Connection conn = ConnectionManager.getConnection()) {
      // Test airlines count
      try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM airlines");
           ResultSet rs = ps.executeQuery()) {
        rs.next();
        System.out.println("Airlines in database: " + rs.getInt(1));
      }

      // Test flights count
      try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM flights");
           ResultSet rs = ps.executeQuery()) {
        rs.next();
        System.out.println("Flights in database: " + rs.getInt(1));
      }
    }
  }
}