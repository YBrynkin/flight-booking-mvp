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
import ru.brynkin.flightbooking.dao.FlightDao;
import ru.brynkin.flightbooking.entity.Airline;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.entity.Flight;
import ru.brynkin.flightbooking.enums.FlightStatus;
import ru.brynkin.flightbooking.exception.DaoException;
import ru.brynkin.flightbooking.util.ConnectionManager;

/**
 * JDBC implementation of the {@link FlightDao} interface that provides CRUD operations
 * for {@link Flight} entities in a PostgreSQL database.
 *
 * <p>This implementation uses prepared statements to prevent SQL injection,
 * manages database connections through {@link ConnectionManager}, and follows
 * the singleton pattern to ensure a single instance throughout the application.</p>
 *
 * <p>It is also worth pointing out that in the implementation of the class for performing select
 * queries, it was decided to use the view for simplified mapping of the Flight entity.</p>
 *
 * @see FlightDao
 * @see Flight
 * @see DaoException
 */


public class FlightDaoImpl implements FlightDao {

  /**
   * The queries assume the use of a view @flight_complete_view for simplified mapping of
   * the Flight entity.
   */

  // View-based queries.
  private static final String FLIGHT_VIEW_BASE_QUERY = String.format("""
          SELECT 
              %s, %s, 
              %s, %s, 
              %s, %s, 
              %s, %s,
              %s, %s,
              %s, %s, 
              %s, %s, 
              %s,
              %s, %s,
              %s, %s,
              %s, %s,
              %s,
              %s, %s,
              %s, %s, 
              %s
          FROM flight_complete_view""",
      FlightViewColumns.FLIGHT_ID, FlightViewColumns.FLIGHT_NUMBER,
      FlightViewColumns.AIRLINE_ID, FlightViewColumns.AIRLINE_NAME,
      FlightViewColumns.AIRLINE_IATA, FlightViewColumns.AIRLINE_ICAO,
      FlightViewColumns.AIRLINE_COUNTRY, FlightViewColumns.AIRLINE_ACTIVE,
      FlightViewColumns.ARRIVAL_AIRPORT_ID, FlightViewColumns.ARRIVAL_AIRPORT_NAME,
      FlightViewColumns.ARRIVAL_CITY, FlightViewColumns.ARRIVAL_COUNTRY,
      FlightViewColumns.ARRIVAL_IATA, FlightViewColumns.ARRIVAL_ICAO,
      FlightViewColumns.ARRIVAL_TIMEZONE,
      FlightViewColumns.DEPARTURE_AIRPORT_ID, FlightViewColumns.DEPARTURE_AIRPORT_NAME,
      FlightViewColumns.DEPARTURE_CITY, FlightViewColumns.DEPARTURE_COUNTRY,
      FlightViewColumns.DEPARTURE_IATA, FlightViewColumns.DEPARTURE_ICAO,
      FlightViewColumns.DEPARTURE_TIMEZONE,
      FlightViewColumns.STATUS_ID, FlightViewColumns.STATUS_NAME,
      FlightViewColumns.DEPARTURE_TIME, FlightViewColumns.ARRIVAL_TIME,
      FlightViewColumns.BASE_PRICE);

  private static final String SELECT_ALL_SQL =
      FLIGHT_VIEW_BASE_QUERY + " ORDER BY departure_time ASC";
  private static final String SELECT_BY_ID_SQL = FLIGHT_VIEW_BASE_QUERY + " WHERE flight_id = ?";
  private static final String SELECT_BY_CRITERIA_SQL = FLIGHT_VIEW_BASE_QUERY + " WHERE 1=1";

  // Table-based queries for writes
  private static final String INSERT_SQL = String.format("""
          INSERT INTO flights (
              %s, %s, %s, %s, %s, %s, %s, %s
          ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)""",
      FlightColumns.FLIGHT_NUMBER, FlightColumns.AIRLINE_ID,
      FlightColumns.DEPARTURE_AIRPORT_ID, FlightColumns.ARRIVAL_AIRPORT_ID,
      FlightColumns.DEPARTURE_TIME, FlightColumns.ARRIVAL_TIME,
      FlightColumns.BASE_PRICE, FlightColumns.STATUS_ID);

  private static final String UPDATE_SQL = String.format("""
          UPDATE flights SET
              %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?
          WHERE %s = ?""",
      FlightColumns.FLIGHT_NUMBER, FlightColumns.AIRLINE_ID,
      FlightColumns.DEPARTURE_AIRPORT_ID, FlightColumns.ARRIVAL_AIRPORT_ID,
      FlightColumns.DEPARTURE_TIME, FlightColumns.ARRIVAL_TIME,
      FlightColumns.BASE_PRICE, FlightColumns.STATUS_ID,
      FlightColumns.FLIGHT_ID);

  private static final String DELETE_SQL =
      String.format("DELETE FROM flights WHERE %s = ?", FlightColumns.FLIGHT_ID);

  private static final String UPDATE_STATUS_SQL = String.format("""
          UPDATE flights SET 
          %S = ? 
          WHERE %S = ?"""
      , FlightColumns.STATUS_ID, FlightColumns.FLIGHT_ID);

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
      sqlBuilder.append(" AND departure_airport_id = ?");
      parameters.add(departureAirportId);
    }

    if (arrivalAirportId != null) {
      sqlBuilder.append(" AND arrival_airport_id = ?");
      parameters.add(arrivalAirportId);
    }

    if (date != null) {
      sqlBuilder.append(" AND DATE(departure_time) = ?");
      parameters.add(Date.valueOf(date));
    }

    if (status != null) {
      sqlBuilder.append(" AND status_id = ?");
      parameters.add(status.ordinal());
    }

    sqlBuilder.append(" ORDER BY departure_time ASC");

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
  public boolean updateStatus(Integer flightId, FlightStatus newStatus) throws DaoException {
    if (flightId == null || flightId <= 0) {
      throw new IllegalArgumentException("Flight ID must be positive");
    }
    if (newStatus == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }

    try (Connection conn = ConnectionManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS_SQL)) {

      stmt.setInt(1, newStatus.ordinal());
      stmt.setInt(2, flightId);

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new DaoException("Failed to update flight status for ID: " + flightId, e);
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
        .flightId(rs.getInt(FlightColumns.FLIGHT_ID))
        .flightNumber(rs.getString(FlightColumns.FLIGHT_NUMBER))
        .airline(mapToAirline(rs))
        .departureAirport(mapToDepartureAirport(rs))
        .arrivalAirport(mapToArrivalAirport(rs))
        .departureTime(rs.getTimestamp(FlightColumns.DEPARTURE_TIME).toLocalDateTime())
        .arrivalTime(rs.getTimestamp(FlightColumns.ARRIVAL_TIME).toLocalDateTime())
        .basePrice(rs.getBigDecimal(FlightColumns.BASE_PRICE))
        .status(FlightStatus.values()[rs.getInt(FlightColumns.STATUS_ID)])
        .build();
  }

  private Airline mapToAirline(ResultSet rs) throws SQLException {
    return Airline.builder()
        .airlineId(rs.getInt(FlightViewColumns.AIRLINE_ID))
        .name(rs.getString(FlightViewColumns.AIRLINE_NAME))
        .iataCode(rs.getString(FlightViewColumns.AIRLINE_IATA))
        .icaoCode(rs.getString(FlightViewColumns.AIRLINE_ICAO))
        .country(rs.getString(FlightViewColumns.AIRLINE_COUNTRY))
        .active(rs.getBoolean(FlightViewColumns.AIRLINE_ACTIVE))
        .build();
  }


  private Airport mapToDepartureAirport(ResultSet rs) throws SQLException {
    return Airport.builder()
        .airportId(rs.getInt(FlightViewColumns.DEPARTURE_AIRPORT_ID))
        .name(rs.getString(FlightViewColumns.DEPARTURE_AIRPORT_NAME))
        .city(rs.getString(FlightViewColumns.DEPARTURE_CITY))
        .country(rs.getString(FlightViewColumns.DEPARTURE_COUNTRY))
        .iataCode(rs.getString(FlightViewColumns.DEPARTURE_IATA))
        .icaoCode(rs.getString(FlightViewColumns.DEPARTURE_ICAO))
        .timezone(rs.getString(FlightViewColumns.DEPARTURE_TIMEZONE))
        .build();
  }

  private Airport mapToArrivalAirport(ResultSet rs) throws SQLException {
    return Airport.builder()
        .airportId(rs.getInt(FlightViewColumns.ARRIVAL_AIRPORT_ID))
        .name(rs.getString(FlightViewColumns.ARRIVAL_AIRPORT_NAME))
        .city(rs.getString(FlightViewColumns.ARRIVAL_CITY))
        .country(rs.getString(FlightViewColumns.ARRIVAL_COUNTRY))
        .iataCode(rs.getString(FlightViewColumns.ARRIVAL_IATA))
        .icaoCode(rs.getString(FlightViewColumns.ARRIVAL_ICAO))
        .timezone(rs.getString(FlightViewColumns.ARRIVAL_TIMEZONE))
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

  private static final class FlightColumns {
    // Flight table columns
    public static final String FLIGHT_ID = "flight_id";
    public static final String FLIGHT_NUMBER = "flight_number";
    public static final String AIRLINE_ID = "airline_id";
    public static final String DEPARTURE_AIRPORT_ID = "departure_airport_id";
    public static final String ARRIVAL_AIRPORT_ID = "arrival_airport_id";
    public static final String DEPARTURE_TIME = "departure_time";
    public static final String ARRIVAL_TIME = "arrival_time";
    public static final String BASE_PRICE = "base_price";
    public static final String STATUS_ID = "status_id";
  }

  private static final class FlightViewColumns {
    // Flight table columns
    public static final String FLIGHT_ID = "flight_id";
    public static final String FLIGHT_NUMBER = "flight_number";
    public static final String AIRLINE_ID = "airline_id";
    public static final String DEPARTURE_AIRPORT_ID = "departure_airport_id";
    public static final String ARRIVAL_AIRPORT_ID = "arrival_airport_id";
    public static final String DEPARTURE_TIME = "departure_time";
    public static final String ARRIVAL_TIME = "arrival_time";
    public static final String BASE_PRICE = "base_price";
    public static final String STATUS_ID = "status_id";

    // Airline table columns
    public static final String AIRLINE_NAME = "airline_name";
    public static final String AIRLINE_IATA = "airline_iata";
    public static final String AIRLINE_ICAO = "airline_icao";
    public static final String AIRLINE_COUNTRY = "airline_country";
    public static final String AIRLINE_ACTIVE = "airline_active";

    // Status columns
    public static final String STATUS_NAME = "status_name";

    // Columns for departure airports
    private static final String DEPARTURE_PREFIX = "departure_";
    public static final String DEPARTURE_AIRPORT_NAME = DEPARTURE_PREFIX + "airport_name";
    public static final String DEPARTURE_CITY = DEPARTURE_PREFIX + "city";
    public static final String DEPARTURE_COUNTRY = DEPARTURE_PREFIX + "country";
    public static final String DEPARTURE_IATA = DEPARTURE_PREFIX + "iata";
    public static final String DEPARTURE_ICAO = DEPARTURE_PREFIX + "icao";
    public static final String DEPARTURE_TIMEZONE = DEPARTURE_PREFIX + "timezone";

    // Columns for arrival airport
    private static final String ARRIVAL_PREFIX = "arrival_";
    public static final String ARRIVAL_AIRPORT_NAME = ARRIVAL_PREFIX + "airport_name";
    public static final String ARRIVAL_CITY = ARRIVAL_PREFIX + "city";
    public static final String ARRIVAL_COUNTRY = ARRIVAL_PREFIX + "country";
    public static final String ARRIVAL_IATA = ARRIVAL_PREFIX + "iata";
    public static final String ARRIVAL_ICAO = ARRIVAL_PREFIX + "icao";
    public static final String ARRIVAL_TIMEZONE = ARRIVAL_PREFIX + "timezone";
  }

}