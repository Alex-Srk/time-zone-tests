package com.example.postgrestimezones.persistence.specifications

data class SearchResult<T>(
	val results: List<T> = emptyList(),
	val totalRecords: Long = 0,
	val totalPages: Int = 0,
	val currentPage: Int = 0,
)
