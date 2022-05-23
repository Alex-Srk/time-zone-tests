package com.example.postgrestimezones

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

interface EventLocalRepository : JpaRepository<EventLocal, Long> {
	fun findByTimestampBefore(timestamp: LocalDateTime): List<EventLocal>
	fun findByTimestampAfter(timestamp: LocalDateTime): List<EventLocal>

	@JvmDefault
	fun findByTimestampAfter(date: LocalDate): List<EventLocal> {
		return findByTimestampAfter(date.atStartOfDay())
	}
}
