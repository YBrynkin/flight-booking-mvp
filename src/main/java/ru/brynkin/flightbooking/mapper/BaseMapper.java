package ru.brynkin.flightbooking.mapper;

/**
 * Generic mapper interface for entity-DTO mapper
 *
 * @param <E> stands for entity
 * @param <D> stands for DTO
 */

public interface BaseMapper<E, D> {
  D toDto(E entity);

  E toEntity(D dto);
}
