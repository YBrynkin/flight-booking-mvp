package ru.brynkin.flightbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for Airport entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportDto {

  private Integer airportId;
  private String name;
  private String city;
  private String country;
  private String iataCode;
  private String icaoCode;
  private String timezone;

}
