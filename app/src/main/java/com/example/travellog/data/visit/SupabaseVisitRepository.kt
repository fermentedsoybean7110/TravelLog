package com.example.travellog.data.visit

import com.example.travellog.domain.model.Visit
import com.example.travellog.domain.model.VisitHistoryItem
import com.example.travellog.domain.repository.VisitRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Serializable
data class VisitRow(
    val id: String,
    @SerialName("city_id") val cityId: Long,
    @SerialName("visited_date") val visitedDate: String,
    val title: String,
    val memo: String?,
) {
    fun toDomain() = Visit(UUID.fromString(id), cityId, LocalDate.parse(visitedDate), title, memo)
}

@Serializable
private data class InsertVisit(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("city_id") val cityId: Long,
    @SerialName("visited_date") val visitedDate: String,
    val title: String,
    val memo: String?,
)

@Serializable
private data class HistoryCountry(val name: String)

@Serializable
private data class HistoryCity(val name: String, val countries: HistoryCountry)

@Serializable
private data class HistoryRow(
    val id: String,
    @SerialName("city_id") val cityId: Long,
    @SerialName("visited_date") val visitedDate: String,
    val title: String,
    val memo: String?,
    val cities: HistoryCity,
) {
    fun toDomain() = VisitHistoryItem(
        Visit(UUID.fromString(id), cityId, LocalDate.parse(visitedDate), title, memo),
        cities.name,
        cities.countries.name,
    )
}

class SupabaseVisitRepository(private val client: SupabaseClient) : VisitRepository {
    override suspend fun getVisitsByCity(cityId: Long): List<Visit> {
        val result = mutableListOf<Visit>()
        var offset = 0L
        while (true) {
            val page = client.from("visits").select {
                filter { eq("city_id", cityId) }
                order("visited_date", Order.DESCENDING)
                order("id", Order.DESCENDING)
                range(offset..offset + PAGE_SIZE - 1)
            }.decodeList<VisitRow>()
            result += page.map(VisitRow::toDomain)
            if (page.size < PAGE_SIZE) return result
            offset += PAGE_SIZE
        }
    }

    override suspend fun getHistory(): List<VisitHistoryItem> {
        val result = mutableListOf<VisitHistoryItem>()
        var offset = 0L
        while (true) {
            val page = client.from("visits").select(
                Columns.raw("id,city_id,visited_date,title,memo,cities(name,countries(name))")
            ) {
                order("visited_date", Order.DESCENDING)
                order("id", Order.DESCENDING)
                range(offset..offset + PAGE_SIZE - 1)
            }.decodeList<HistoryRow>()
            result += page.map(HistoryRow::toDomain)
            if (page.size < PAGE_SIZE) return result
            offset += PAGE_SIZE
        }
    }

    override suspend fun create(visit: Visit) {
        val userId = requireNotNull(client.auth.currentUserOrNull()?.id) { "No authenticated user" }
        client.from("visits").insert(
            InsertVisit(visit.id.toString(), userId, visit.cityId, visit.visitedDate.toString(), visit.title, visit.memo)
        )
    }

    override suspend fun update(visit: Visit) {
        val updated = client.from("visits").update({
            set("visited_date", visit.visitedDate.toString())
            set("title", visit.title)
            set("memo", visit.memo)
        }) {
            select()
            filter { eq("id", visit.id.toString()) }
        }.decodeList<VisitRow>()
        check(updated.size == 1) { "Visit was not updated" }
    }

    override suspend fun delete(id: UUID) {
        val deleted = client.from("visits").delete {
            select()
            filter { eq("id", id.toString()) }
        }.decodeList<VisitRow>()
        check(deleted.size == 1) { "Visit was not deleted" }
    }

    private companion object {
        const val PAGE_SIZE = 100L
    }
}
