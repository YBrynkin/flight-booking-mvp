package ru.brynkin.flightbooking.services.impl;

import java.util.List;
import java.util.Optional;
import ru.brynkin.flightbooking.dao.AirportDao;
import ru.brynkin.flightbooking.dto.AirportDto;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.exception.DaoException;
import ru.brynkin.flightbooking.mapper.AirportMapper;
import ru.brynkin.flightbooking.services.AirportService;

/**
 * Implementation of {@link AirportService} interface that provides business logic operations
 * for managing airports in the flight booking system.
 * <p>
 * This service acts as an intermediary between controllers and the data access layer,
 * handling the conversion between entities and DTOs while performing airport-related
 * operations.
 * </p>
 *
 * @see AirportService
 * @see Airport
 * @see AirportDto
 */

public class AirportServiceImpl implements AirportService {

  private final AirportDao airportDao;
  private final AirportMapper airportMapper;

  public AirportServiceImpl(AirportDao airportDao, AirportMapper airportMapper) {
    this.airportDao = airportDao;
    this.airportMapper = airportMapper;
  }

  @Override
  public List<AirportDto> getAllAirports() throws DaoException {
    List<Airport> airports = airportDao.findAll();
    return airports.stream()
        .map(airportMapper::toDto)
        .toList();
  }

  @Override
  public AirportDto getAirportById(Integer id) throws DaoException {
    Optional<Airport> airport = airportDao.findById(id);
    return airport.map(airportMapper::toDto)
        .orElseThrow(() -> new DaoException("Airport not found"));
  }

  @Override
  public List<AirportDto> getAirportsByCountry(String country) throws DaoException {
    List<Airport> airports = airportDao.findByCountry(country);
    return airports.stream()
        .map(airportMapper::toDto)
        .toList();
  }

  @Override
  public List<AirportDto> getAirportsByCity(String city) throws DaoException {
    List<Airport> airports = airportDao.findByCity(city);
    return airports.stream()
        .map(airportMapper::toDto)
        .toList();
  }

  @Override
  public AirportDto getAirportByIataCode(String iataCode) throws DaoException {
    return airportDao.findByIataCode(iataCode)
        .map(airportMapper::toDto)
        .orElseThrow(() -> new DaoException("Airport with IATA code " + iataCode + " not found"));
  }

  @Override
  public AirportDto getAirportByIcaoCode(String icaoCode) throws DaoException {
    return airportDao.findByIcaoCode(icaoCode)
        .map(airportMapper::toDto)
        .orElseThrow(() -> new DaoException("Airport with ICAO code " + icaoCode + " not found"));
  }

  @Override
  public AirportDto addAirport(Airport airport) throws DaoException {
    Airport createdAirport = airportDao.create(airport);
    return airportMapper.toDto(createdAirport);
  }

  @Override
  public AirportDto updateAirport(Airport airport) throws DaoException {
    Airport updatedAirport = airportDao.update(airport);
    return airportMapper.toDto(updatedAirport);
  }

  @Override
  public boolean deleteAirport(Integer id) throws DaoException {
    return airportDao.delete(id);
  }
}