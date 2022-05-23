package com.example.postgrestimezones.persistence.specifications.criteria

internal data class JpaSpecificationSearchCriterion(
	val propertyName: String,
	val operation: SearchOperation,
	val value: Any,
	val enumClass: String? = null,
)
