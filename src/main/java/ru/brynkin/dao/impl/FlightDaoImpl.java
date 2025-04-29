package ru.brynkin.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import ru.brynkin.dao.FlightDao;
import ru.brynkin.entity.Flight;
import ru.brynkin.exception.DaoException;
import ru.brynkin.util.ConnectionManager;

public class FlightDaoImpl implements FlightDao {

  private static final String GET_ALL_FLIGHTS_SQL = """
      SELECT * FROM
      flights
      """;

  private static final String GET_FLIGHT_BY_ID_SQL = """
      SELECT * FROM
      flights 
      WHERE flight_id = ?
      """;

  private static final String ADD_FLIGHT_SQL = """
      INSERT INTO
      flights (flight_number, airline_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time, base_price, status_id) 
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      """;

  private static FlightDaoImpl INSTANCE = new FlightDaoImpl();

  public static FlightDaoImpl getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new FlightDaoImpl();
    }
    return INSTANCE;
  }

  @Override
  public Optional<Flight> findById(Integer key) throws DaoException {
    return Optional.empty();
  }

  @Override
  public List<Flight> findAll() throws DaoException {

    try (Connection connection = ConnectionManager.getConnection();
         var stmt = connection.prepareStatement(GET_ALL_FLIGHTS_SQL);) {
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        Flight flight = new Flight();
        flight.setFlightNumber(rs.getString(1));
        flight.setAirlineId(rs.getInt(2));
        flight.setDepartureAirportId(rs.getInt(3));
        flight.setArrivalAirportId(rs.getInt(4));
        //Need to add other values
      }
      return List.of();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
    }
  }

  @Override
  public Flight create(Flight entity) throws DaoException {
    return null;
  }

  @Override
  public Flight update(Flight entity) throws DaoException {
    return null;
  }

  @Override
  public boolean delete(Integer key) throws DaoException {
    return false;
  }
}
