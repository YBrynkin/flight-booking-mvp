package ru.brynkin.flightbooking.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.brynkin.flightbooking.dao.AirportDao;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.exception.DaoException;
import ru.brynkin.flightbooking.util.ConnectionManager;

/**
 * JDBC implementation of the {@link AirportDao} interface that provides CRUD operations
 * for {@link Airport} entities in a PostgreSQL database.
 *
 * <p>This implementation uses prepared statements to prevent SQL injection,
 * manages database connections through {@link ConnectionManager}, and follows
 * the singleton pattern to ensure a single instance throughout the application.</p>
 *
 * @see AirportDao
 * @see Airport
 * @see DaoException
 */

public class AirportDaoImpl implements AirportDao {

  // SQL Query templates
  private static final String BASE_SELECT = """
      SELECT %s, %s, %s, %s, %s, %s, %s 
      FROM airports""".formatted(
      Columns.AIRPORT_ID, Columns.NAME, Columns.CITY,
      Columns.COUNTRY, Columns.IATA_CODE, Columns.ICAO_CODE, Columns.TIMEZONE);

  private static final String SELECT_ALL_SQL = BASE_SELECT;

  private static final String SELECT_BY_ID_SQL =
      BASE_SELECT + " WHERE " + Columns.AIRPORT_ID + " = ?";

  private static final String SELECT_BY_COUNTRY_SQL =
      BASE_SELECT + " WHERE " + Columns.COUNTRY + " = ?";

  private static final String SELECT_BY_CITY_SQL = BASE_SELECT + " WHERE " + Columns.CITY + " = ?";

  private static final String SELECT_BY_IATA_CODE_SQL =
      BASE_SELECT + " WHERE " + Columns.IATA_CODE + " = ?";

  private static final String SELECT_BY_ICAO_CODE_SQL =
      BASE_SELECT + " WHERE " + Columns.ICAO_CODE + " = ?";

  private static final String INSERT_SQL = """
      INSERT INTO airports (%s, %s, %s, %s, %s, %s) 
      VALUES (?, ?, ?, ?, ?, ?)""".formatted(
      Columns.NAME, Columns.CITY, Columns.COUNTRY,
      Columns.IATA_CODE, Columns.ICAO_CODE, Columns.TIMEZONE);

  private static final String UPDATE_SQL = """
      UPDATE airports 
      SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? 
      WHERE %s = ?""".formatted(
      Columns.NAME, Columns.CITY, Columns.COUNTRY,
      Columns.IATA_CODE, Columns.ICAO_CODE, Columns.TIMEZONE,
      Columns.AIRPORT_ID);

  private static final String DELETE_SQL =
      "DELETE FROM airports WHERE " + Columns.AIRPORT_ID + " = ?";

  // Singleton pattern
  private static AirportDaoImpl instance;


  private AirportDaoImpl() {
    // Private constructor to prevent instantiation
  }

  // Get-method
  public static AirportDaoImpl getInstance() {
    if (instance == null) {
      instance = new AirportDaoImpl();
    }
    return instance;
  }

  @Override
  public Optional<Airport> findById(Integer id) throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {

      stmt.setInt(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? Optional.of(mapRowToAirport(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to find airport by ID: " + id, e);
    }
  }

  @Override
  public List<Airport> findAll() throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

      List<Airport> airports = new ArrayList<>();
      while (rs.next()) {
        airports.add(mapRowToAirport(rs));
      }
      return airports;
    } catch (SQLException e) {
      throw new DaoException("Failed to retrieve all airports", e);
    }
  }

  @Override
  public Airport create(Airport airport) throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(
             INSERT_SQL,
             Statement.RETURN_GENERATED_KEYS)) {

      setAirportParameters(stmt, airport);
      stmt.executeUpdate();

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          airport.setAirportId(generatedKeys.getInt("airport_id"));
        }
        return airport;
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to create airport", e);
    }
  }

  @Override
  public Airport update(Airport airport) throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {

      setAirportParameters(stmt, airport);
      stmt.setInt(7, airport.getAirportId());

      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new DaoException("No airport found with ID: " + airport.getAirportId());
      }
      return airport;
    } catch (SQLException e) {
      throw new DaoException("Failed to update airport with ID: " + airport.getAirportId(), e);
    }
  }

  @Override
  public boolean delete(Integer id) throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {

      stmt.setInt(1, id);
      int affectedRows = stmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      throw new DaoException("Failed to delete airport with ID: " + id, e);
    }
  }

  @Override
  public List<Airport> findByCountry(String country) throws DaoException {
    return executeQueryWithParameter(SELECT_BY_COUNTRY_SQL, country);
  }

  @Override
  public List<Airport> findByCity(String city) throws DaoException {
    return executeQueryWithParameter(SELECT_BY_CITY_SQL, city);
  }

  @Override
  public Optional<Airport> findByIataCode(String iataCode) throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(SELECT_BY_IATA_CODE_SQL)) {

      stmt.setString(1, iataCode);

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? Optional.of(mapRowToAirport(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to find airport by IATA code: " + iataCode, e);
    }
  }

  @Override
  public Optional<Airport> findByIcaoCode(String icaoCode) throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ICAO_CODE_SQL)) {

      stmt.setString(1, icaoCode);

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? Optional.of(mapRowToAirport(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to find airport by ICAO code: " + icaoCode, e);
    }
  }

  private List<Airport> executeQueryWithParameter(String sql, String parameter)
      throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(sql)) {

      stmt.setString(1, parameter);

      try (ResultSet rs = stmt.executeQuery()) {
        List<Airport> airports = new ArrayList<>();
        while (rs.next()) {
          airports.add(mapRowToAirport(rs));
        }
        return airports;
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to execute query with parameter: " + parameter, e);
    }
  }

  private Airport mapRowToAirport(ResultSet rs) throws SQLException {
    return Airport.builder()
        .airportId(rs.getInt(Columns.AIRPORT_ID))
        .name(rs.getString(Columns.NAME))
        .city(rs.getString(Columns.CITY))
        .country(rs.getString(Columns.COUNTRY))
        .iataCode(rs.getString(Columns.IATA_CODE))
        .icaoCode(rs.getString(Columns.ICAO_CODE))
        .timezone(rs.getString(Columns.TIMEZONE))
        .build();
  }

  private void setAirportParameters(PreparedStatement stmt, Airport airport) throws DaoException {
    try {
      stmt.setString(1, airport.getName());
      stmt.setString(2, airport.getCity());
      stmt.setString(3, airport.getCountry());
      stmt.setString(4, airport.getIataCode());
      stmt.setString(5, airport.getIcaoCode());
      stmt.setString(6, airport.getTimezone());
    } catch (SQLException e) {
      throw new DaoException("Failed to set airport parameters", e);
    }

  }

  // Column name constants
  private static final class Columns {
    static final String AIRPORT_ID = "airport_id";
    static final String NAME = "name";
    static final String CITY = "city";
    static final String COUNTRY = "country";
    static final String IATA_CODE = "iata_code";
    static final String ICAO_CODE = "icao_code";
    static final String TIMEZONE = "timezone";
  }

}


