package com.example.travellog.domain.usecase

import com.example.travellog.domain.model.Visit
import com.example.travellog.domain.repository.VisitRepository
import java.util.UUID

class GetVisitsByCityUseCase(private val repository: VisitRepository) {
    suspend operator fun invoke(cityId: Long) = repository.getVisitsByCity(cityId)
}

class GetVisitHistoryUseCase(private val repository: VisitRepository) {
    suspend operator fun invoke() = repository.getHistory()
}

class SaveVisitUseCase(private val repository: VisitRepository) {
    suspend operator fun invoke(visit: Visit, isNew: Boolean) {
        require(visit.title.isNotBlank())
        if (isNew) repository.create(visit) else repository.update(visit)
    }
}

class DeleteVisitUseCase(private val repository: VisitRepository) {
    suspend operator fun invoke(id: UUID) = repository.delete(id)
}
