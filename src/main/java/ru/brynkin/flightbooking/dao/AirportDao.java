package ru.brynkin.flightbooking.dao;

import java.util.List;
import java.util.Optional;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.exception.DaoException;

/**
 * Data Access Object interface for {@link Airport} entities.
 * Provides CRUD operations and additional methods for querying airports by various criteria.
 *
 * <p>Extends {@link BaseDao} with {@code Integer} as the key type and {@code Airport} as the entity type.</p>
 *
 * @see Airport
 * @see BaseDao
 * @see DaoException
 */

public interface AirportDao extends BaseDao<Integer, Airport> {

  List<Airport> findByCountry(String country) throws DaoException;

  List<Airport> findByCity(String city) throws DaoException;

  Optional<Airport> findByIataCode(String iataCode) throws DaoException;

  Optional<Airport> findByIcaoCode(String icaoCode) throws DaoException;
}
