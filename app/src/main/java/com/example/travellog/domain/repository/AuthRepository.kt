package com.example.travellog.domain.repository

interface AuthRepository {
    suspend fun hasSession(): Boolean
    suspend fun login(email: String, password: String)
    suspend fun logout()
}
