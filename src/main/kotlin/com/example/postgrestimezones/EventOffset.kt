package com.example.postgrestimezones

import org.hibernate.annotations.OptimisticLock
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.PreUpdate
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Version

@Entity
@Table(name = "events_offset")
@SequenceGenerator(name = "EventOffset", sequenceName = "events_offset_seq", allocationSize = 1)
class EventOffset(
	@Column
	val description: String,
	@Column
	val timestamp: OffsetDateTime,
	@Column
	val dueDate: LocalDate = LocalDate.now(),
) {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EventOffset")
	val id: Long? = null

	@Version
	@Column(name = "entity_version", nullable = false)
	open val entityVersion: Long? = null

	@OptimisticLock(excluded = true)
	@Column(name = "entity_created", nullable = false)
	open val created: OffsetDateTime = OffsetDateTime.now()

	@OptimisticLock(excluded = true)
	@Column(name = "entity_updated", nullable = false)
	open var updated: OffsetDateTime = OffsetDateTime.now()

	@PreUpdate
	fun onPreUpdate() {
		updated = OffsetDateTime.now()
	}

	override fun toString(): String {
		return """|
			|Event(id=$id, created='$created', updated='$updated', version='$entityVersion',
			|timestamp='$timestamp', dueDate='$dueDate'
			|description='$description')
			|""".trimMargin()
	}
}
