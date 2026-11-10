package com.example.travellog.di

import com.example.travellog.BuildConfig
import com.example.travellog.data.auth.AuthRepositoryImpl
import com.example.travellog.data.country.CountryDataSource
import com.example.travellog.data.country.CountryRepositoryImpl
import com.example.travellog.data.stub.StubAuthRepository
import com.example.travellog.data.stub.StubCountryRepository
import com.example.travellog.domain.repository.AuthRepository
import com.example.travellog.domain.repository.CountryRepository
import com.example.travellog.domain.usecase.GetCountriesUseCase
import com.example.travellog.presentation.country.CountryViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_PUBLISHABLE_KEY) {
            install(Auth)
            install(Postgrest)
        }
    }
    single<AuthRepository> {
        if (BuildConfig.DEBUG && BuildConfig.USE_STUB) StubAuthRepository() else AuthRepositoryImpl(get())
    }
    single { CountryDataSource(get()) }
    single<CountryRepository> {
        if (BuildConfig.DEBUG && BuildConfig.USE_STUB) StubCountryRepository() else CountryRepositoryImpl(get())
    }
    factory { GetCountriesUseCase(get()) }
    viewModel { CountryViewModel(get(), get()) }
}
