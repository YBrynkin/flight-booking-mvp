package ru.brynkin.flightbooking.services.impl;

import java.util.List;
import ru.brynkin.flightbooking.dto.AirportDto;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.services.AirportService;

public class AirportServiceImpl implements AirportService {

  private static AirportService instance = new AirportServiceImpl();

  public static AirportService getInstance() {
    if (instance == null) {
      instance = new AirportServiceImpl();
    }
    return instance;
  }

  @Override
  public List<AirportDto> getAllAirports() {
    return List.of();
  }

  @Override
  public AirportDto getAirportById(Integer id) {
    return null;
  }

  @Override
  public AirportDto addAirport(Airport airport) {
    return null;
  }

  @Override
  public AirportDto updateAirport(Airport airport) {
    return null;
  }

  @Override
  public void deleteAirport(Integer id) {

  }
}
