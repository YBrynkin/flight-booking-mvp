package ru.brynkin.flightbooking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enitty class for arilines table
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Airline {

  Integer airlineId;
  String name;
  String iataCode;
  String icaoCode;
  String country;
  Boolean active;
}
