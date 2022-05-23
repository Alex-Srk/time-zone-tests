package com.example.postgrestimezones

import com.example.postgrestimezones.persistence.BaseEntity
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "events_local")
@SequenceGenerator(name = "EventLocal", sequenceName = "events_local_seq", allocationSize = 1)
class EventLocal(
	@Column
	val description: String,
	@Column
	val timestamp: LocalDateTime,
	@Column
	val dueDate: LocalDate = LocalDate.now(),
) : BaseEntity<Long>() {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EventLocal")
	override var id: Long? = null
	override fun toString(): String {
		return """|
			|Event(id=$id, created='$created', updated='$updated', version='$entityVersion',
			|timestamp='$timestamp', dueDate='$dueDate'
			|description='$description')
			|""".trimMargin()
	}
}
