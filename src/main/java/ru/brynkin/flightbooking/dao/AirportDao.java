package ru.brynkin.flightbooking.dao;

import java.util.List;
import java.util.Optional;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.exception.DaoException;

/**
 * Intraface for Airport's DAO class
 */

public interface AirportDao extends BaseDao<Integer, Airport> {

  List<Airport> findByCountry(String country) throws DaoException;

  List<Airport> findByCity(String city) throws DaoException;

  Optional<Airport> findByIataCode(String iataCode) throws DaoException;

  Optional<Airport> findByIcaoCode(String icaoCode) throws DaoException;
}
