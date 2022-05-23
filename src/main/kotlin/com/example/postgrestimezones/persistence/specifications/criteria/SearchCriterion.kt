package com.example.postgrestimezones.persistence.specifications.criteria

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY,
	property = "type",
	defaultImpl = StringSearchCriterion::class
)
@JsonSubTypes(
	JsonSubTypes.Type(value = StringSearchCriterion::class, name = "string"),
	JsonSubTypes.Type(value = DateSearchCriterion::class, name = "date"),
	JsonSubTypes.Type(value = DateTimeSearchCriterion::class, name = "datetime"),
	JsonSubTypes.Type(value = UUIDSearchCriterion::class, name = "uuid"),
	JsonSubTypes.Type(value = EnumSearchCriterion::class, name = "enum"),
	JsonSubTypes.Type(value = BooleanSearchCriterion::class, name = "boolean")
)
sealed class SearchCriterion(
	val type: String,
	open val propertyName: String,
	open val operation: String,
	open val value: Any,
	open val enumClass: String? = null,
) {
	internal fun toJpaSpecificationSearchCriterion(): JpaSpecificationSearchCriterion {
		val searchOperation = checkNotNull(SearchOperation.valueFrom(operation)) {
			"Operation $operation is not supported for searching"
		}
		return JpaSpecificationSearchCriterion(propertyName, searchOperation, value, enumClass)
	}
}

data class EnumSearchCriterion(
	override val propertyName: String,
	override val operation: String,
	override val value: String,
	override val enumClass: String,
) : SearchCriterion(
	type = "enum",
	propertyName = propertyName,
	operation = operation,
	value = value,
	enumClass = enumClass
)

data class StringSearchCriterion(
	override val propertyName: String,
	override val operation: String,
	override val value: String,
) : SearchCriterion(type = "string", propertyName = propertyName, operation = operation, value = value)

data class UUIDSearchCriterion(
	override val propertyName: String,
	override val operation: String,
	override val value: UUID,
) : SearchCriterion(type = "uuid", propertyName = propertyName, operation = operation, value = value)

data class DateSearchCriterion(
	override val propertyName: String,
	override val operation: String,
	override val value: LocalDate,
) : SearchCriterion(type = "date", propertyName = propertyName, operation = operation, value = value)

data class DateTimeSearchCriterion(
	override val propertyName: String,
	override val operation: String,
	override val value: LocalDateTime,
) : SearchCriterion(type = "datetime", propertyName = propertyName, operation = operation, value = value)

data class BooleanSearchCriterion(
	override val propertyName: String,
	override val operation: String,
	override val value: Boolean,
) : SearchCriterion(type = "boolean", propertyName = propertyName, operation = operation, value = value)
