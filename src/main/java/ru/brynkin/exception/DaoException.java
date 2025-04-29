package ru.brynkin.exception;

/**
 * Custom exception class for Data Access Object (DAO) layer exceptions.
 * Wraps underlying SQLExceptions and provides meaningful error messages.
 */
public class DaoException extends Exception {

  /**
   * Constructs a new DAO exception with the specified detail message.
   *
   * @param message the detail message
   */
  public DaoException(String message) {
    super(message);
  }

  /**
   * Constructs a new DAO exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the underlying cause (usually a SQLException)
   */
  public DaoException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new DAO exception with the specified cause.
   *
   * @param cause the underlying cause (usually a SQLException)
   */
  public DaoException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new DAO exception with the specified detail message,
   * cause, suppression enabled or disabled, and writable stack trace enabled or disabled.
   *
   * @param message            the detail message
   * @param cause              the underlying cause
   * @param enableSuppression  whether suppression is enabled or disabled
   * @param writableStackTrace whether the stack trace should be writable
   */
  protected DaoException(String message, Throwable cause,
                         boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}