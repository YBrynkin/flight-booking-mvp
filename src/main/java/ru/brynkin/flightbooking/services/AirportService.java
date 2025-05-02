package ru.brynkin.flightbooking.services;

import java.util.List;
import ru.brynkin.flightbooking.dto.AirportDto;
import ru.brynkin.flightbooking.entity.Airport;

/**
 * General interface of airport service
 */

public interface AirportService {

  List<AirportDto> getAllAirports();

  AirportDto getAirportById(Integer id);

  AirportDto addAirport(Airport airport);

  AirportDto updateAirport(Airport airport);

  void deleteAirport(Integer id);
}
