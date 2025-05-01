package ru.brynkin.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.brynkin.dao.AirportsDao;
import ru.brynkin.entity.Airport;
import ru.brynkin.exception.DaoException;
import ru.brynkin.util.ConnectionManager;

public class AirportDaoImpl implements AirportsDao {

  // SQL Queries with explicit column names
  private static final String SELECT_ALL_AIRPORTS_SQL = """
      SELECT 
          airport_id, 
          name, 
          city, 
          country, 
          iata_code, 
          icao_code, 
          timezone 
      FROM airports""";

  private static final String SELECT_AIRPORT_BY_ID_SQL = """
      SELECT 
          airport_id, 
          name, 
          city, 
          country, 
          iata_code, 
          icao_code, 
          timezone 
      FROM airports 
      WHERE airport_id = ?""";

  private static final String SELECT_BY_COUNTRY_SQL = """
      SELECT 
          airport_id, 
          name, 
          city, 
          country, 
          iata_code, 
          icao_code, 
          timezone 
      FROM airports 
      WHERE country = ?""";

  private static final String SELECT_BY_CITY_SQL = """
      SELECT 
          airport_id, 
          name, 
          city, 
          country, 
          iata_code, 
          icao_code, 
          timezone 
      FROM airports 
      WHERE city = ?""";

  private static final String SELECT_BY_IATA_CODE_SQL = """
      SELECT 
          airport_id, 
          name, 
          city, 
          country, 
          iata_code, 
          icao_code, 
          timezone 
      FROM airports 
      WHERE iata_code = ?""";

  private static final String SELECT_BY_ICAO_CODE_SQL = """
      SELECT 
          airport_id, 
          name, 
          city, 
          country, 
          iata_code, 
          icao_code, 
          timezone 
      FROM airports 
      WHERE icao_code = ?""";

  private static final String INSERT_TO_TABLE_SQL = """
      INSERT INTO airports (
          name, 
          city, 
          country, 
          iata_code, 
          icao_code, 
          timezone
      ) VALUES (?, ?, ?, ?, ?, ?)""";

  private static final String UPDATE_AIRPORT_SQL = """
      UPDATE airports SET
          name = ?,
          city = ?,
          country = ?,
          iata_code = ?,
          icao_code = ?,
          timezone = ?
      WHERE airport_id = ?""";

  private static final String DELETE_AIRPORT_BY_ID_SQL = """
      DELETE FROM airports
      WHERE airport_id = ?""";

  // Singleton pattern with proper synchronization
  private static volatile AirportDaoImpl instance;

  private AirportDaoImpl() {
    // Private constructor to prevent instantiation
  }

  public static AirportDaoImpl getInstance() {
    if (instance == null) {
      synchronized (AirportDaoImpl.class) {
        if (instance == null) {
          instance = new AirportDaoImpl();
        }
      }
    }
    return instance;
  }

  @Override
  public Optional<Airport> findById(Integer id) throws DaoException {
    try (Connection connection = ConnectionManager.getConnection();
         PreparedStatement stmt = connection.prepareStatement(SELECT_AIRPORT_BY_ID_SQL)) {

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
         ResultSet rs = stmt.executeQuery(SELECT_ALL_AIRPORTS_SQL)) {

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
             INSERT_TO_TABLE_SQL,
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
         PreparedStatement stmt = connection.prepareStatement(UPDATE_AIRPORT_SQL)) {

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
         PreparedStatement stmt = connection.prepareStatement(DELETE_AIRPORT_BY_ID_SQL)) {

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

  private Airport mapRowToAirport(ResultSet rs) throws DaoException {
    try {
      return Airport.builder()
          .airportId(rs.getInt("airport_id"))
          .name(rs.getString("name"))
          .city(rs.getString("city"))
          .country(rs.getString("country"))
          .iataCode(rs.getString("iata_code"))
          .icaoCode(rs.getString("icao_code"))
          .timezone(rs.getString("timezone"))
          .build();
    } catch (SQLException e) {
      throw new DaoException("Failed to map result set to Airport entity", e);
    }
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
}