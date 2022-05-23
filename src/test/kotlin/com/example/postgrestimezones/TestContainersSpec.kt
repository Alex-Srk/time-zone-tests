package com.example.postgrestimezones

import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.LogConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import io.kotest.core.spec.style.StringSpec
import liquibase.pro.packaged.it
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName


//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//interface TestContainersTest {
//	companion object {
//		private val container = startTestDatabaseContainer()
//
//		@JvmStatic
//		@DynamicPropertySource
//		fun dbProperties(registry: DynamicPropertyRegistry) {
//			registry.injectProperties(container)
//		}
//	}
//}

@SpringBootTest
abstract class TestContainersSpec(body: StringSpec.() -> Unit = {}) : StringSpec(body) {
	companion object {
		private val container = startTestDatabaseContainer()

		@JvmStatic
		@DynamicPropertySource
		fun dbProperties(registry: DynamicPropertyRegistry) {
			registry.injectProperties(container)
		}
	}

}

private fun startTestDatabaseContainer(): PostgreSQLContainer<*> {
	val hostPort = 45432
	val containerExposedPort = 5432
	val cmdModifier: (CreateContainerCmd) -> Unit = {
		it.hostConfig?.withPortBindings(
			PortBinding(
				Ports.Binding.bindPort(hostPort),
				ExposedPort(containerExposedPort)))
	}

	return PostgreSQLContainer(DockerImageName.parse("postgres:11-alpine"))
		.withExposedPorts(containerExposedPort)
		.withCreateContainerCmdModifier(cmdModifier)
		.withDatabaseName("loansV2Test")
		.withUsername("loansV2Test")
		.withPassword("loansV2Test")
		.withEnv("TZ", "UTC")
		.withCommand("postgres", "-c", "log_statement=all")
		.also { it.start() }
}

private fun DynamicPropertyRegistry.injectProperties(container: PostgreSQLContainer<*>) {
	add("spring.datasource.url", container::getJdbcUrl)
	add("spring.datasource.password", container::getPassword)
	add("spring.datasource.username", container::getUsername)
}

