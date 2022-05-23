package com.example.postgrestimezones.persistence.specifications.criteria

import com.example.postgrestimezones.persistence.specifications.JpaSpecificationBuilder
import com.example.postgrestimezones.persistence.specifications.SearchResult
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.function.Consumer

class QueryCriteriaConsumer<U, T> : Consumer<QueryCriteria> {
	private lateinit var queryCriteria: QueryCriteria
	private var specification: Specification<U>? = null
	private lateinit var page: PageRequest

	override fun accept(queryCriteria: QueryCriteria) {
		this.queryCriteria = queryCriteria
		this.specification = queryCriteria.criteria
			.fold(JpaSpecificationBuilder<U>(queryCriteria.joinStrategy)) { builder, criterion ->
				builder.with(criterion)
			}.build()

		val sortingCriterion = queryCriteria.sortingCriterion
		this.page = PageRequest.of(
			queryCriteria.page - 1,
			queryCriteria.pageSize,
			Sort.by(sortingCriterion.sortDirection, sortingCriterion.propertyName)
		)
	}

	fun queryResults(repository: JpaSpecificationExecutor<U>, transformer: (U) -> T): SearchResult<T> {
		val searchResult = repository.findAll(specification, page)
			.map { transformer(it) }
		return SearchResult(
			searchResult.content,
			searchResult.totalElements,
			searchResult.totalPages,
			queryCriteria.page
		)
	}
}

fun <U, T> JpaSpecificationExecutor<U>.searchWith(criteria: QueryCriteria, transformer: (U) -> T): SearchResult<T> {
	val queryConsumer = QueryCriteriaConsumer<U, T>()
	queryConsumer.accept(criteria)
	return queryConsumer.queryResults(this, transformer)
}
