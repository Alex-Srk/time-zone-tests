package com.example.postgrestimezones.persistence


import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.OptimisticLock
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.persistence.PreUpdate
import javax.persistence.Transient
import javax.persistence.Version

private const val STATIC_HASHCODE: Int = 17

@MappedSuperclass
@BatchSize(size = 10)
@TypeDefs(
	TypeDef(name = JpaType.JSON, typeClass = JsonBinaryType::class),
	TypeDef(name = JpaType.JSONB, typeClass = JsonBinaryType::class)
)
abstract class BaseEntity<ID : Serializable> {

	@Version
	@Column(name = "entity_version", nullable = false)
	open val entityVersion: Long? = null

	@OptimisticLock(excluded = true)
	@Column(name = "entity_created", nullable = false)
	open val created: Instant = Instant.now()

	@OptimisticLock(excluded = true)
	@Column(name = "entity_updated", nullable = false)
	open var updated: Instant = Instant.now()

	/**
	 * Direct use of `id` is strongly discouraged in business level code, this should only be used on the persistence
	 * level by the framework and related infrastructure code.
	 */
	@Suppress("PropertyName")
	protected abstract var id: ID?

	/**
	 * Accessing `savedId` implies that entity has been retrieved from a repository or crafted with a pre-set identifier.
	 * If this condition is not met, accessing it will generate an `IllegalStateException`.
	 */
	val savedId: ID
		@Transient get() = checkNotNull(id) { "Entity has not been saved" }

	override fun toString(): String = "${javaClass.simpleName} { id: $id }"

	override fun equals(other: Any?): Boolean = when {
		this === other -> true
		javaClass != other?.let(ProxyUtils::getUserClass) -> false
		else -> {
			other as BaseEntity<*>
			id != null && id == other.id
		}
	}

	@PreUpdate
	fun onPreUpdate() {
		updated = Instant.now()
	}

	override fun hashCode(): Int = STATIC_HASHCODE + ((this.id?.hashCode() ?: 0) * 31)
}
