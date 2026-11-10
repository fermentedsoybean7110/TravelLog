package com.example.travellog.data.auth

import com.example.travellog.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(private val client: SupabaseClient) : AuthRepository {
    override suspend fun hasSession(): Boolean {
        val status = client.auth.sessionStatus.first { it !is SessionStatus.Initializing }
        return when (status) {
            is SessionStatus.Authenticated -> true
            is SessionStatus.NotAuthenticated -> false
            is SessionStatus.RefreshFailure -> throw IllegalStateException("Session refresh failed")
            SessionStatus.Initializing -> error("Session initialization did not finish")
        }
    }

    override suspend fun login(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun logout() {
        client.auth.signOut()
    }
}
