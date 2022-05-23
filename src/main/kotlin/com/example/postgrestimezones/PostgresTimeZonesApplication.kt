package com.example.postgrestimezones

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.TimeZone

@SpringBootApplication
class PostgresTimeZonesApplication

fun main(args: Array<String>) {
	runApplication<PostgresTimeZonesApplication>(*args)
}
