package com.example.postgrestimezones

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

interface EventOffsetRepository : JpaRepository<EventOffset, Long> {
	fun findByTimestampBefore(timestamp: OffsetDateTime): List<EventOffset>
	fun findByTimestampAfter(timestamp: OffsetDateTime): List<EventOffset>

	@JvmDefault
	fun findByTimestampAfter(date: LocalDate): List<EventOffset> {
		println(date)
		return findByTimestampAfter(date.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toOffsetDateTime())
	}
}
