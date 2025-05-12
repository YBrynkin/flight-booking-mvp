package ru.brynkin.flightbooking.mapper.impl;

import ru.brynkin.flightbooking.dto.AirportDto;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.mapper.AirportMapper;

/**
 * Implementation of {@link AirportMapper} interface that provides conversion functionality
 * between {@link Airport} entities and {@link AirportDto} data transfer objects.
 * <p>
 * This mapper follows the singleton pattern to ensure a single instance is used
 * throughout the application for consistent mapping behavior.
 * </p>
 *
 * @see AirportMapper
 * @see Airport
 * @see AirportDto
 */

public class AirportMapperImpl implements AirportMapper {

  //Singleton pattern
  private static AirportMapperImpl instance;

  private AirportMapperImpl() {
  }

  public static AirportMapper getInstance() {
    if (instance == null) {
      instance = new AirportMapperImpl();
    }
    return instance;
  }

  @Override
  public AirportDto toDto(Airport airport) {
    return AirportDto.builder()
        .airportId(airport.getAirportId())
        .name(airport.getName())
        .city(airport.getCity())
        .country(airport.getCountry())
        .iataCode(airport.getIataCode())
        .icaoCode(airport.getIcaoCode())
        .timezone(airport.getTimezone())
        .build();

  }

  @Override
  public Airport toEntity(AirportDto dto) {
    return Airport.builder()
        .airportId(dto.getAirportId())
        .name(dto.getName())
        .city(dto.getCity())
        .country(dto.getCountry())
        .iataCode(dto.getIataCode())
        .icaoCode(dto.getIcaoCode())
        .timezone(dto.getTimezone())
        .build();
  }
}
