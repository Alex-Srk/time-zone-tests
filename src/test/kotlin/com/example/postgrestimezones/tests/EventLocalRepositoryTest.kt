package com.example.postgrestimezones.tests

import com.example.postgrestimezones.EventLocal
import com.example.postgrestimezones.EventLocalRepository
import com.example.postgrestimezones.TestContainersSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.springframework.data.domain.Sort
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class EventLocalRepositoryTest(
	repository: EventLocalRepository,
) : TestContainersSpec({

	println("zone: ${ZoneId.systemDefault()}")

	val timestamp = LocalDateTime.now()
	println("now: $timestamp")

	beforeSpec {
		val event = EventLocal(description = "Second local event", timestamp = timestamp)
		repository.save(event)
	}

	"should list all events" {
		val events = repository.findAll()
		events shouldHaveSize 2
	}

	"should select timestamp with correct local" {
		val correct = LocalDateTime.of(2022, 2, 23, 2, 2, 22)
		val event = repository.findAll(Sort.by(Sort.Direction.ASC, "id")).first()

		printDetails(correct)
		printDetails(event.timestamp)

//		assertSoftly {
			event.timestamp.shouldBeEqualComparingTo(correct)
			event.created.atZone(ZoneId.systemDefault()).toLocalDateTime().isEqual(correct).shouldBe(true)
//		}
	}

	"should find first event but not second" {
		val events = repository.findByTimestampBefore(timestamp.minusMinutes(10))
		events.shouldHaveSize(1)
		events.first().description shouldStartWith "First"
	}

	"should find second event but not first" {
		val events = repository.findByTimestampAfter(timestamp.minusMinutes(10))
		events.shouldHaveSize(1)
		events.first().description shouldStartWith "Second"
		events.first().timestamp shouldBe timestamp
	}

	"should find both" {
		val events = repository.findByTimestampBefore(timestamp.plusSeconds(1))
		events.shouldHaveSize(2)
		println(events)
	}

	"should find today's event" {
		val events = repository.findByTimestampAfter(LocalDate.now())
		events.shouldHaveSize(1)
		events.first().description shouldContain "Second"
		events.first().timestamp shouldBe timestamp
		println(events)
	}
})

private fun printDetails(timestamp: LocalDateTime) {
	println("day: ${timestamp.dayOfMonth}, hour: ${timestamp.hour}")
}
