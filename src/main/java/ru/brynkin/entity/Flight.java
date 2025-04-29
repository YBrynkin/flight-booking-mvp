package ru.brynkin.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity class for flights table
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Flight {

  private Integer flightId;
  private String flightNumber;
  private Integer airlineId;
  private Integer departureAirportId;
  private Integer arrivalAirportId;
  private LocalDateTime departureTime;
  private LocalDateTime arrivalTime;
  private BigDecimal price;
  private Integer statusId;


}
