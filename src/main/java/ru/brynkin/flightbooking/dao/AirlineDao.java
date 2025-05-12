package ru.brynkin.flightbooking.dao;

import ru.brynkin.flightbooking.entity.Airline;
import ru.brynkin.flightbooking.exception.DaoException;

/**
 * Data Access Object interface for {@link Airline} entities.
 * Provides CRUD operations and additional methods for querying airports by various criteria.
 *
 * <p>Extends {@link BaseDao} with {@code Integer} as the key type and {@code Airline} as the entity type.</p>
 *
 * @see Airline
 * @see BaseDao
 * @see DaoException
 */

public interface AirlineDao extends BaseDao<Integer, Airline> {


}
