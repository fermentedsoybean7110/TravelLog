package com.example.travellog.domain.model

import java.time.LocalDate
import java.util.UUID

data class Visit(
    val id: UUID,
    val cityId: Long,
    val visitedDate: LocalDate,
    val title: String,
    val memo: String?,
)

data class VisitHistoryItem(
    val visit: Visit,
    val cityName: String,
    val countryName: String,
)
