package com.example.travellog.domain.repository

import com.example.travellog.domain.model.Visit
import com.example.travellog.domain.model.VisitHistoryItem
import java.util.UUID

interface VisitRepository {
    suspend fun getVisitsByCity(cityId: Long): List<Visit>
    suspend fun getHistory(): List<VisitHistoryItem>
    suspend fun create(visit: Visit)
    suspend fun update(visit: Visit)
    suspend fun delete(id: UUID)
}
