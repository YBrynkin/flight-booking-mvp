package ru.brynkin.flightbooking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity class for airports table
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Airport {

  private Integer airportId;
  private String name;
  private String city;
  private String country;
  private String iataCode;
  private String icaoCode;
  private String timezone;

}
