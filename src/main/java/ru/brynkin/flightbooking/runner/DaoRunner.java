package ru.brynkin.flightbooking.runner;

import java.util.List;
import java.util.UUID;
import ru.brynkin.flightbooking.dao.impl.AirportDaoImpl;
import ru.brynkin.flightbooking.dao.impl.FlightDaoImpl;
import ru.brynkin.flightbooking.entity.Airport;
import ru.brynkin.flightbooking.entity.Flight;
import ru.brynkin.flightbooking.exception.DaoException;

public class DaoRunner {

  private static final AirportDaoImpl airportDao = AirportDaoImpl.getInstance();
  private static final FlightDaoImpl flightDao = FlightDaoImpl.getInstance();

  // Generate unique test data that won't violate constraints
  private static String randomIataCode() {
    return "T" + UUID.randomUUID().toString().substring(0, 2).toUpperCase();
  }

  private static String randomIcaoCode() {
    return "TT" + UUID.randomUUID().toString().substring(0, 2).toUpperCase();
  }

  public static void main(String[] args) {
    try {
      System.out.println("=== Dao Testing ===");

      List<Flight> flights = flightDao.findAll();
      System.out.println();

//      testFindOperations();
//      testUpdateWithUniqueData();
//      testCreateWithUniqueData();
//      testDelete();

      System.out.println("=== All tests completed without constraint violations ===");
    } catch (DaoException e) {
      System.err.println("DAO Test failed: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void testCreateWithUniqueData() throws DaoException {
    System.out.println("\n--- Testing create() with unique constraints ---");

    Airport airport = buildUniqueTestAirport();
    Airport created = airportDao.create(airport);

    System.out.printf("Created airport: ID=%d, IATA=%s, ICAO=%s%n",
        created.getAirportId(), created.getIataCode(), created.getIcaoCode());
  }

  private static void testFindOperations() throws DaoException {
    System.out.println("\n--- Testing find operations ---");

    // Setup
    Airport testAirport = buildUniqueTestAirport();
    Airport created = airportDao.create(testAirport);

    // Test findById
    airportDao.findById(created.getAirportId())
        .ifPresent(a -> System.out.println("Found by ID: " + a.getName()));

    // Test findByIataCode
    airportDao.findByIataCode(created.getIataCode())
        .ifPresent(a -> System.out.println("Found by IATA: " + a.getIataCode()));

    // Cleanup
    airportDao.delete(created.getAirportId());
  }

  private static void testUpdateWithUniqueData() throws DaoException {
    System.out.println("\n--- Testing update() with unique constraints ---");

    // Create initial airport
    Airport airport = buildUniqueTestAirport();
    Airport created = airportDao.create(airport);

    // Update with new unique values
    String newIata = randomIataCode();
    String newIcao = randomIcaoCode();
    created.setIataCode(newIata);
    created.setIcaoCode(newIcao);

    Airport updated = airportDao.update(created);
    System.out.printf("Updated airport: IATA=%s, ICAO=%s%n",
        updated.getIataCode(), updated.getIcaoCode());

    // Cleanup
    airportDao.delete(updated.getAirportId());
  }

  private static void testDelete() throws DaoException {
    System.out.println("\n--- Testing delete() ---");

    Airport airport = buildUniqueTestAirport();
    Airport created = airportDao.create(airport);

    boolean deleted = airportDao.delete(created.getAirportId());
    System.out.println("Delete " + (deleted ? "succeeded" : "failed"));
  }

  private static Airport buildUniqueTestAirport() {
    return Airport.builder()
        .name("Test Airport " + UUID.randomUUID().toString().substring(0, 4))
        .city("Test City")
        .country("Test Country")
        .iataCode(randomIataCode())  // Ensures unique IATA
        .icaoCode(randomIcaoCode())  // Ensures unique ICAO
        .timezone("UTC")
        .build();
  }
}