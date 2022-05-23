package com.example.postgrestimezones.persistence.specifications

import com.example.postgrestimezones.persistence.specifications.criteria.JpaSpecificationSearchCriterion
import com.example.postgrestimezones.persistence.specifications.criteria.OperationType
import com.example.postgrestimezones.persistence.specifications.criteria.SearchOperation
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

internal class CustomJpaSpecification<U>(
	val criterion: JpaSpecificationSearchCriterion,
) : Specification<U> {

	override fun toPredicate(
		root: Root<U>,
		query: CriteriaQuery<*>,
		criteriaBuilder: CriteriaBuilder,
	): Predicate? {
		return when (criterion.operation.type) {
			OperationType.UNARY -> resolveUnaryPredicate(root, criteriaBuilder)
			OperationType.BINARY -> resolveBinaryPredicate(root, criteriaBuilder)
			OperationType.TERNARY -> resolveTernaryPredicate(root, criteriaBuilder)
		}
	}

	private fun resolveUnaryPredicate(root: Root<U>, criteriaBuilder: CriteriaBuilder): Predicate? {
		return when (criterion.operation) {
			SearchOperation.UNDEFINED -> criteriaBuilder.isNull(getPropertyPath(root, criterion.propertyName))
			SearchOperation.DEFINED -> criteriaBuilder.isNotNull(getPropertyPath(root, criterion.propertyName))
			SearchOperation.EMPTY -> criteriaBuilder.or(
				criteriaBuilder.isNull(getPropertyPath(root, criterion.propertyName)),
				criteriaBuilder.equal(getPropertyPath(root, criterion.propertyName), "")
			)
			else -> null
		}
	}

	private fun getPropertyPath(root: Root<U>, propertyName: String): Path<Any> {
		val propertyPaths = propertyName.split(".")
		if (propertyPaths.size == 1) {
			return root.get(propertyName)
		}

		val rootPathJoin = root.join<Any, Any>(propertyPaths.first())
		val parentPath = propertyPaths.drop(1).dropLast(1)
			.fold(rootPathJoin) { currentJoin, newPath ->
				currentJoin.join(newPath)
			}

		return parentPath.get(propertyPaths.last())
	}

	private fun resolveBinaryPredicate(root: Root<U>, criteriaBuilder: CriteriaBuilder): Predicate? {
		@Suppress("UNCHECKED_CAST")
		return when (criterion.operation) {
			SearchOperation.EQUALITY -> criteriaBuilder.enumAwarePredicate(root, CriteriaBuilder::equal)
			SearchOperation.NEGATION -> criteriaBuilder.enumAwarePredicate(root, CriteriaBuilder::notEqual)
			SearchOperation.LESS_THAN -> criteriaBuilder.lessThan(
				getPropertyPath(root, criterion.propertyName) as Path<Comparable<Any>>,
				criterion.value as Comparable<Any>
			)
			SearchOperation.LESS_THAN_OR_EQUALS -> criteriaBuilder.lessThanOrEqualTo(
				getPropertyPath(root, criterion.propertyName) as Path<Comparable<Any>>,
				criterion.value as Comparable<Any>
			)
			SearchOperation.GREATER_THAN -> criteriaBuilder.greaterThan(
				getPropertyPath(root, criterion.propertyName) as Path<Comparable<Any>>,
				criterion.value as Comparable<Any>
			)
			SearchOperation.GREATER_THAN_OR_EQUALS -> criteriaBuilder.greaterThanOrEqualTo(
				getPropertyPath(root, criterion.propertyName) as Path<Comparable<Any>>,
				criterion.value as Comparable<Any>
			)
			SearchOperation.STARTS_WITH -> criteriaBuilder.like(
				getPropertyPath(root, criterion.propertyName) as Path<String>,
				"${criterion.value}%"
			)
			SearchOperation.ENDS_WITH -> criteriaBuilder.like(
				getPropertyPath(root, criterion.propertyName) as Path<String>,
				"%${criterion.value}"
			)
			SearchOperation.CONTAINS -> criteriaBuilder.like(
				getPropertyPath(root, criterion.propertyName) as Path<String>,
				"%${criterion.value}%"
			)
			SearchOperation.NOT_CONTAINS -> criteriaBuilder.like(
				getPropertyPath(root, criterion.propertyName) as Path<String>,
				"%${criterion.value}%"
			).not()
			else -> null
		}
	}

	private fun CriteriaBuilder.enumAwarePredicate(
		root: Root<U>,
		searchOperation: CriteriaBuilder.(expression: Expression<*>, value: Any) -> Predicate,
	): Predicate? {
		return if (criterion.enumClass != null) {
			val enumObject =
				Class.forName(criterion.enumClass).enumConstants.first { (it as Enum<*>).name == criterion.value }
			searchOperation(getPropertyPath(root, criterion.propertyName), enumObject)
		} else {
			searchOperation(getPropertyPath(root, criterion.propertyName), criterion.value)
		}
	}

	private fun resolveTernaryPredicate(root: Root<U>, criteriaBuilder: CriteriaBuilder): Predicate? {
		return when (criterion.operation) {
			SearchOperation.PERIOD -> TODO("Ternary operation on dates")
			SearchOperation.RANGE_INCLUDING -> TODO("Ternary operation on a range of numbers")
			else -> null
		}
	}
}

