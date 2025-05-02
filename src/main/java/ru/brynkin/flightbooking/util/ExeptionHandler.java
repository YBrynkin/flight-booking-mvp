package ru.brynkin.flightbooking.util;

/**
 * Exeption handler utility class
 */

public final class ExeptionHandler {

  public static void handleException(String message, Exception e) {
    System.err.println(message);
    e.printStackTrace();

  }
}
