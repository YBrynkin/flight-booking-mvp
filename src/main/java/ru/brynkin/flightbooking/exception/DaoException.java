package ru.brynkin.flightbooking.exception;

/**
 * Custom exception class for Data Access Object (DAO) layer exceptions.
 * Wraps underlying SQLExceptions and provides meaningful error messages.
 */
public class DaoException extends Exception {

  public DaoException(String message) {
    super(message);
  }

  public DaoException(String message, Throwable cause) {
    super(message, cause);
  }

  public DaoException(Throwable cause) {
    super(cause);
  }

}