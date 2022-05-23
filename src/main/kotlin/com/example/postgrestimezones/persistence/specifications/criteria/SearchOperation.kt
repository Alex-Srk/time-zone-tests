package com.example.postgrestimezones.persistence.specifications.criteria

enum class SearchOperation(val operation: String, val type: OperationType) {
	EQUALITY("=", OperationType.BINARY),
	NEGATION("<>", OperationType.BINARY),
	LESS_THAN("<", OperationType.BINARY),
	LESS_THAN_OR_EQUALS("<=", OperationType.BINARY),
	GREATER_THAN(">", OperationType.BINARY),
	GREATER_THAN_OR_EQUALS(">=", OperationType.BINARY),
	UNDEFINED("not defined", OperationType.UNARY),
	DEFINED("is defined", OperationType.UNARY),
	EMPTY("empty", OperationType.UNARY),
	STARTS_WITH("starts with", OperationType.BINARY),
	ENDS_WITH("ends with", OperationType.BINARY),
	CONTAINS("contains", OperationType.BINARY),
	NOT_CONTAINS("not contains", OperationType.BINARY),
	PERIOD("period", OperationType.TERNARY),
	RANGE_INCLUDING("range including", OperationType.TERNARY);

	companion object {
		fun valueFrom(operation: String) =
			values().find { it.operation.equals(operation, true) }
	}
}

enum class OperationType {
	UNARY,
	BINARY,
	TERNARY
}
