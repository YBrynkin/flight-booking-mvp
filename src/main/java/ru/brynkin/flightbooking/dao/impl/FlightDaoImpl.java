package ru.brynkin.flightbooking.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import ru.brynkin.flightbooking.dao.FlightDao;
import ru.brynkin.flightbooking.entity.Airline;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.entity.Flight;
import ru.brynkin.flightbooking.enums.FlightStatus;
import ru.brynkin.flightbooking.exception.DaoException;
import ru.brynkin.flightbooking.util.ConnectionManager;

@Slf4j
public class FlightDaoImpl implements FlightDao {

  private static final String SELECT_ALL_SQL = """
      SELECT 
          f.flight_id, f.flight_number, f.departure_time, f.arrival_time,
          f.base_price, f.status_id,
          al.airline_id, al.name as airline_name, al.iata_code as airline_iata, 
          al.icao_code as airline_icao, al.country as airline_country, al.is_active as airline_active,
          dep.airport_id as dep_id, dep.name as dep_name, dep.city as dep_city, 
          dep.country as dep_country, dep.iata_code as dep_iata, dep.icao_code as dep_icao, dep.timezone as dep_tz,
          arr.airport_id as arr_id, arr.name as arr_name, arr.city as arr_city, 
          arr.country as arr_country, arr.iata_code as arr_iata, arr.icao_code as arr_icao, arr.timezone as arr_tz
      FROM flights f
      JOIN airlines al ON f.airline_id = al.airline_id
      JOIN airports dep ON f.departure_airport_id = dep.airport_id
      JOIN airports arr ON f.arrival_airport_id = arr.airport_id
      ORDER BY f.departure_time ASC""";

  private static final String SELECT_BY_ID_SQL = """
      SELECT 
          f.flight_id, f.flight_number, f.departure_time, f.arrival_time,
          f.base_price, f.status_id,
          al.airline_id, al.name as airline_name, al.iata_code as airline_iata, 
          al.icao_code as airline_icao, al.country as airline_country, al.is_active as airline_active,
          dep.airport_id as dep_id, dep.name as dep_name, dep.city as dep_city, 
          dep.country as dep_country, dep.iata_code as dep_iata, dep.icao_code as dep_icao, dep.timezone as dep_tz,
          arr.airport_id as arr_id, arr.name as arr_name, arr.city as arr_city, 
          arr.country as arr_country, arr.iata_code as arr_iata, arr.icao_code as arr_icao, arr.timezone as arr_tz
      FROM flights f
      JOIN airlines al ON f.airline_id = al.airline_id
      JOIN airports dep ON f.departure_airport_id = dep.airport_id
      JOIN airports arr ON f.arrival_airport_id = arr.airport_id
      WHERE f.flight_id = ?""";

  private static final String SELECT_BY_CRITERIA_SQL = """
      SELECT 
          f.flight_id, f.flight_number, f.departure_time, f.arrival_time,
          f.base_price, f.status_id,
          al.airline_id, al.name as airline_name, al.iata_code as airline_iata, 
          al.icao_code as airline_icao, al.country as airline_country, al.is_active as airline_active,
          dep.airport_id as dep_id, dep.name as dep_name, dep.city as dep_city, 
          dep.country as dep_country, dep.iata_code as dep_iata, dep.icao_code as dep_icao, dep.timezone as dep_tz,
          arr.airport_id as arr_id, arr.name as arr_name, arr.city as arr_city, 
          arr.country as arr_country, arr.iata_code as arr_iata, arr.icao_code as arr_icao, arr.timezone as arr_tz
      FROM flights f
      JOIN airlines al ON f.airline_id = al.airline_id
      JOIN airports dep ON f.departure_airport_id = dep.airport_id
      JOIN airports arr ON f.arrival_airport_id = arr.airport_id
      WHERE 1=1""";

  private static final String INSERT_SQL = """
      INSERT INTO flights (
          flight_number, airline_id, departure_airport_id, arrival_airport_id,
          departure_time, arrival_time, base_price, status_id
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

  private static final String UPDATE_SQL = """
      UPDATE flights SET
          flight_number = ?,
          airline_id = ?,
          departure_airport_id = ?,
          arrival_airport_id = ?,
          departure_time = ?,
          arrival_time = ?,
          base_price = ?,
          status_id = ?
      WHERE flight_id = ?""";

  private static final String UPDATE_STATUS_SQL = """
      UPDATE flights SET status_id = ? 
      WHERE flight_id = ?""";

  private static final String DELETE_SQL = "DELETE FROM flights WHERE flight_id = ?";

  // Singleton pattern
  private static volatile FlightDaoImpl instance;

  private FlightDaoImpl() {
    // Private constructor to prevent instantiation
  }

  public static FlightDaoImpl getInstance() {
    if (instance == null) {
      synchronized (FlightDaoImpl.class) {
        if (instance == null) {
          instance = new FlightDaoImpl();
        }
      }
    }
    return instance;
  }

  @Override
  public List<Flight> findAll() throws DaoException {
    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
         ResultSet rs = stmt.executeQuery()) {

      List<Flight> flights = new ArrayList<>();
      while (rs.next()) {
        flights.add(mapToFlight(rs));
      }
      return flights;
    } catch (SQLException e) {
      throw new DaoException("Failed to find all flights", e);
    }
  }

  @Override
  public Optional<Flight> findById(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new IllegalArgumentException("Flight ID must be positive");
    }

    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

      stmt.setInt(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next() ? Optional.of(mapToFlight(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to find flight by ID: " + id, e);
    }
  }

  @Override
  public List<Flight> findByCriteria(Integer departureAirportId, Integer arrivalAirportId,
                                     LocalDate date, FlightStatus status) throws DaoException {
    StringBuilder sqlBuilder = new StringBuilder(SELECT_BY_CRITERIA_SQL);
    List<Object> parameters = new ArrayList<>();

    if (departureAirportId != null) {
      sqlBuilder.append(" AND f.departure_airport_id = ?");
      parameters.add(departureAirportId);
    }

    if (arrivalAirportId != null) {
      sqlBuilder.append(" AND f.arrival_airport_id = ?");
      parameters.add(arrivalAirportId);
    }

    if (date != null) {
      sqlBuilder.append(" AND DATE(f.departure_time) = ?");
      parameters.add(Date.valueOf(date));
    }

    if (status != null) {
      sqlBuilder.append(" AND f.status_id = ?");
      parameters.add(status.ordinal());
    }

    sqlBuilder.append(" ORDER BY f.departure_time ASC");

    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

      for (int i = 0; i < parameters.size(); i++) {
        stmt.setObject(i + 1, parameters.get(i));
      }

      try (ResultSet rs = stmt.executeQuery()) {
        List<Flight> flights = new ArrayList<>();
        while (rs.next()) {
          flights.add(mapToFlight(rs));
        }
        return flights;
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to find flights by criteria", e);
    }
  }

  @Override
  public Flight create(Flight flight) throws DaoException {
    validateFlight(flight);

    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(INSERT_SQL,
             Statement.RETURN_GENERATED_KEYS)) {

      setFlightParameters(stmt, flight);
      stmt.executeUpdate();

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          flight.setFlightId(generatedKeys.getInt(1));
          return flight;
        }
        throw new DaoException("Failed to retrieve generated flight ID");
      }
    } catch (SQLException e) {
      throw new DaoException("Failed to save flight", e);
    }
  }

  @Override
  public Flight update(Flight flight) throws DaoException {
    validateFlight(flight);
    if (flight.getFlightId() == null) {
      throw new IllegalArgumentException("Flight ID is required for update");
    }

    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

      setFlightParameters(stmt, flight);
      stmt.setLong(9, flight.getFlightId());

      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        throw new DaoException("No flight found with ID: " + flight.getFlightId());
      }
      return flight;
    } catch (SQLException e) {
      throw new DaoException("Failed to update flight with ID: " + flight.getFlightId(), e);
    }
  }

  @Override
  public boolean updateStatus(Long flightId, FlightStatus newStatus) throws DaoException {
    if (flightId == null || flightId <= 0) {
      throw new IllegalArgumentException("Flight ID must be positive");
    }
    if (newStatus == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }

    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS_SQL)) {

      stmt.setInt(1, newStatus.ordinal());
      stmt.setLong(2, flightId);

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new DaoException("Failed to update flight status", e);
    }
  }

  @Override
  public boolean delete(Integer id) throws DaoException {
    if (id == null || id <= 0) {
      throw new IllegalArgumentException("Flight ID must be positive");
    }

    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

      stmt.setInt(1, id);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new DaoException("Failed to delete flight with ID: " + id, e);
    }
  }

  private Flight mapToFlight(ResultSet rs) throws SQLException {
    return Flight.builder()
        .flightId(rs.getInt("flight_id"))
        .flightNumber(rs.getString("flight_number"))
        .airline(mapToAirline(rs))
        .departureAirport(mapToDepartureAirport(rs))
        .arrivalAirport(mapToArrivalAirport(rs))
        .departureTime(rs.getTimestamp("departure_time").toLocalDateTime())
        .arrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime())
        .basePrice(rs.getBigDecimal("base_price"))
        .status(FlightStatus.values()[rs.getInt("status_id")])
        .build();
  }

  private Airline mapToAirline(ResultSet rs) throws SQLException {
    return Airline.builder()
        .airlineId(rs.getInt("airline_id"))
        .name(rs.getString("airline_name"))
        .iataCode(rs.getString("airline_iata"))
        .icaoCode(rs.getString("airline_icao"))
        .country(rs.getString("airline_country"))
        .active(rs.getBoolean("airline_active"))
        .build();
  }

  private Airport mapToDepartureAirport(ResultSet rs) throws SQLException {
    return Airport.builder()
        .airportId(rs.getInt("dep_id"))
        .name(rs.getString("dep_name"))
        .city(rs.getString("dep_city"))
        .country(rs.getString("dep_country"))
        .iataCode(rs.getString("dep_iata"))
        .icaoCode(rs.getString("dep_icao"))
        .timezone(rs.getString("dep_tz"))
        .build();
  }

  private Airport mapToArrivalAirport(ResultSet rs) throws SQLException {
    return Airport.builder()
        .airportId(rs.getInt("arr_id"))
        .name(rs.getString("arr_name"))
        .city(rs.getString("arr_city"))
        .country(rs.getString("arr_country"))
        .iataCode(rs.getString("arr_iata"))
        .icaoCode(rs.getString("arr_icao"))
        .timezone(rs.getString("arr_tz"))
        .build();
  }

  private void setFlightParameters(PreparedStatement stmt, Flight flight) throws SQLException {
    stmt.setString(1, flight.getFlightNumber());
    stmt.setInt(2, flight.getAirline().getAirlineId());
    stmt.setInt(3, flight.getDepartureAirport().getAirportId());
    stmt.setInt(4, flight.getArrivalAirport().getAirportId());
    stmt.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
    stmt.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));
    stmt.setBigDecimal(7, flight.getBasePrice());
    stmt.setInt(8, flight.getStatus().ordinal());
  }

  private void validateFlight(Flight flight) {
    if (flight == null) {
      throw new IllegalArgumentException("Flight cannot be null");
    }
    if (flight.getArrivalTime().isBefore(flight.getDepartureTime())) {
      throw new IllegalArgumentException("Arrival time must be after departure time");
    }
    if (flight.getAirline() == null || flight.getAirline().getAirlineId() == null) {
      throw new IllegalArgumentException("Airline is required");
    }
    if (flight.getDepartureAirport() == null ||
        flight.getDepartureAirport().getAirportId() == null) {
      throw new IllegalArgumentException("Departure airport is required");
    }
    if (flight.getArrivalAirport() == null || flight.getArrivalAirport().getAirportId() == null) {
      throw new IllegalArgumentException("Arrival airport is required");
    }
  }
}