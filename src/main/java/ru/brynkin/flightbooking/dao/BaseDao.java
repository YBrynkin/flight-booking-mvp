package ru.brynkin.flightbooking.dao;

import java.util.List;
import java.util.Optional;
import ru.brynkin.flightbooking.exception.DaoException;

/**
 * Generic DAO interface for CRUD operations
 *
 * @param <T> Entity type
 * @param <K> Primary key type
 */
public interface BaseDao<K, T> {

  Optional<T> findById(K key) throws DaoException;

  List<T> findAll() throws DaoException;

  T create(T entity) throws DaoException;

  T update(T entity) throws DaoException;

  boolean delete(K key) throws DaoException;
}
