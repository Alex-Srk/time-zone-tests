package com.example.postgrestimezones.persistence.specifications

import com.example.postgrestimezones.persistence.specifications.criteria.CriteriaJoinStrategy
import com.example.postgrestimezones.persistence.specifications.criteria.JpaSpecificationSearchCriterion
import com.example.postgrestimezones.persistence.specifications.criteria.SearchCriterion
import com.example.postgrestimezones.persistence.specifications.criteria.SearchOperation
import org.springframework.data.jpa.domain.Specification

internal class JpaSpecificationBuilder<U>(
	private val joinStrategy: CriteriaJoinStrategy,
) {
	private val criteria: MutableList<JpaSpecificationSearchCriterion> = mutableListOf()

	fun with(parameterName: String, operation: String, value: Any): JpaSpecificationBuilder<U> {
		SearchOperation.valueFrom(operation)?.let { searchOperation ->
			criteria += JpaSpecificationSearchCriterion(
				parameterName,
				searchOperation,
				value
			)
		}
		return this
	}

	fun with(criterion: SearchCriterion): JpaSpecificationBuilder<U> {
		criteria += criterion.toJpaSpecificationSearchCriterion()
		return this
	}

	fun with(searchCriterion: JpaSpecificationSearchCriterion): JpaSpecificationBuilder<U> {
		criteria += searchCriterion
		return this
	}

	fun with(customJpaSpecification: CustomJpaSpecification<U>): JpaSpecificationBuilder<U> {
		criteria += customJpaSpecification.criterion
		return this
	}

	fun build(): Specification<U>? {
		return if (criteria.size > 0) {
			val specification = Specification.where(CustomJpaSpecification<U>(criteria.first()))
			criteria.drop(1)
				.fold(specification) { previousSpecifications, currentCriterion: JpaSpecificationSearchCriterion ->
					if (joinStrategy == CriteriaJoinStrategy.AND) {
						previousSpecifications.and(CustomJpaSpecification<U>(currentCriterion))
					} else {
						previousSpecifications.or(CustomJpaSpecification<U>(currentCriterion))
					}
				}
		} else {
			null
		}
	}
}
