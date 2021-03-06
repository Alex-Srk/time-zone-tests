package com.example.postgrestimezones.persistence.specifications

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.io.Serializable

@NoRepositoryBean
interface ReadOnlyRepository<T, ID : Serializable?> : Repository<T, ID> {

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be null.
	 * @return the entity with the given id or Optional#empty() if none found.
	 */
	fun findById(id: ID): T?

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be null.
	 * @return true if an entity with the given id exists, false otherwise.
	 */
	fun existsById(id: ID): Boolean

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	fun findAll(): Iterable<T>

	/**
	 * Returns all instances of the type `T` with the given IDs.
	 * If some or all ids are not found, no entities are returned for these IDs.
	 * Note that the order of elements in the result is not guaranteed.
	 *
	 * @param ids must not be null nor contain any null values.
	 * @return guaranteed to be not null. The size can be equal or less than the number of given ids.
	 */
	fun findAllById(ids: Iterable<ID>): Iterable<T>

	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities.
	 */
	fun count(): Long
}
