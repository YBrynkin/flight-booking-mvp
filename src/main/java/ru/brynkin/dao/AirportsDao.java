package ru.brynkin.dao;

import java.util.List;
import java.util.Optional;
import ru.brynkin.entity.Airport;
import ru.brynkin.exception.DaoException;

/**
 * Intraface for Airport's DAO class
 */

public interface AirportsDao extends BaseDao<Integer, Airport> {

  List<Airport> findByCountry(String country) throws DaoException;

  List<Airport> findByCity(String city) throws DaoException;

  Optional<Airport> findByIataCode(String iataCode) throws DaoException;

  Optional<Airport> findByIcaoCode(String icaoCode) throws DaoException;
}
