package com.example.postgrestimezones.persistence.specifications.criteria

import com.example.postgrestimezones.persistence.specifications.SearchResult
import org.springframework.data.domain.Sort

data class QueryCriteria(
	val page: Int = 1,
	val pageSize: Int = 20,
	val sortingCriterion: SortingCriterion = SortingCriterion("id"),
	val criteria: Set<SearchCriterion> = emptySet(),
	val joinStrategy: CriteriaJoinStrategy = CriteriaJoinStrategy.AND,
)

data class SortingCriterion(
	val propertyName: String,
	val sortDirection: Sort.Direction = Sort.Direction.DESC,
)

enum class CriteriaJoinStrategy {
	AND, OR
}

fun <U, T> SearchResult<U>.map(transformer: (U) -> T): SearchResult<T> =
	SearchResult(
		totalPages = totalPages,
		currentPage = currentPage,
		totalRecords = totalRecords,
		results = results.map(transformer)
	)
