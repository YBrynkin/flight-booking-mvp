package ru.brynkin.flightbooking.dao;

import java.time.LocalDate;
import java.util.List;
import ru.brynkin.flightbooking.entity.Flight;
import ru.brynkin.flightbooking.enums.FlightStatus;
import ru.brynkin.flightbooking.exception.DaoException;

public interface FlightDao extends BaseDao<Integer, Flight> {
  List<Flight> findByCriteria(Integer departureAirportId, Integer arrivalAirportId,
                              LocalDate date, FlightStatus status) throws DaoException;

  boolean updateStatus(Long flightId, FlightStatus newStatus) throws DaoException;

}
