package ru.brynkin.flightbooking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.brynkin.flightbooking.enums.FlightStatus;

/**
 * Entity class for flights table
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
  private Integer flightId;
  private String flightNumber;
  private Airline airline;
  private Airport departureAirport;
  private Airport arrivalAirport;
  private LocalDateTime departureTime;
  private LocalDateTime arrivalTime;
  private BigDecimal basePrice;
  private FlightStatus status;
}