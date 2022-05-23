package com.example.postgrestimezones

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringExtension

class KotestConfig : AbstractProjectConfig() {
	override fun extensions(): List<Extension> = listOf(SpringExtension)
}
