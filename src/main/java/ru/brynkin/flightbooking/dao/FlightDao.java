package ru.brynkin.flightbooking.dao;

import java.time.LocalDate;
import java.util.List;
import ru.brynkin.flightbooking.entity.Flight;
import ru.brynkin.flightbooking.enums.FlightStatus;
import ru.brynkin.flightbooking.exception.DaoException;

/**
 * Data Access Object interface for {@link Flight} entities.
 * Provides CRUD operations and additional methods for querying airports by various criteria.
 *
 * <p>Extends {@link BaseDao} with {@code Integer} as the key type and {@code Flight} as the entity type.</p>
 *
 * @see Flight
 * @see BaseDao
 * @see DaoException
 */

public interface FlightDao extends BaseDao<Integer, Flight> {

  List<Flight> findByCriteria(Integer departureAirportId, Integer arrivalAirportId,
                              LocalDate date, FlightStatus status) throws DaoException;

  boolean updateStatus(Integer flightId, FlightStatus newStatus) throws DaoException;

}
