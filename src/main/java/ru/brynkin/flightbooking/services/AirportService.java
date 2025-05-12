package ru.brynkin.flightbooking.services;

import java.util.List;
import ru.brynkin.flightbooking.dto.AirportDto;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.exception.DaoException;

/**
 * Service interface for airport operations
 */
public interface AirportService {

  List<AirportDto> getAllAirports() throws DaoException;

  AirportDto getAirportById(Integer id) throws DaoException;

  List<AirportDto> getAirportsByCountry(String country) throws DaoException;

  List<AirportDto> getAirportsByCity(String city) throws DaoException;

  AirportDto getAirportByIataCode(String iataCode) throws DaoException;

  AirportDto getAirportByIcaoCode(String icaoCode) throws DaoException;

  AirportDto addAirport(Airport airport) throws DaoException;

  AirportDto updateAirport(Airport airport) throws DaoException;

  boolean deleteAirport(Integer id) throws DaoException;
}