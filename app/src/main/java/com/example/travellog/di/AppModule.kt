package com.example.travellog.di

import com.example.travellog.BuildConfig
import com.example.travellog.data.auth.AuthRepositoryImpl
import com.example.travellog.data.country.CountryDataSource
import com.example.travellog.data.country.CountryRepositoryImpl
import com.example.travellog.data.city.CityRepositoryImpl
import com.example.travellog.data.stub.StubAuthRepository
import com.example.travellog.data.stub.StubCityRepository
import com.example.travellog.data.stub.StubCountryRepository
import com.example.travellog.data.stub.StubVisitRepository
import com.example.travellog.data.visit.SupabaseVisitRepository
import com.example.travellog.domain.repository.AuthRepository
import com.example.travellog.domain.repository.CityRepository
import com.example.travellog.domain.repository.CountryRepository
import com.example.travellog.domain.repository.VisitRepository
import com.example.travellog.domain.usecase.GetCountriesUseCase
import com.example.travellog.domain.usecase.GetCitiesUseCase
import com.example.travellog.domain.usecase.GetVisitedCitiesUseCase
import com.example.travellog.domain.usecase.GetVisitsByCityUseCase
import com.example.travellog.domain.usecase.GetVisitHistoryUseCase
import com.example.travellog.domain.usecase.SaveVisitUseCase
import com.example.travellog.domain.usecase.DeleteVisitUseCase
import com.example.travellog.presentation.country.CountryViewModel
import com.example.travellog.presentation.city.CityViewModel
import com.example.travellog.presentation.visit.VisitViewModel
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
    single<CityRepository> {
        if (BuildConfig.DEBUG && BuildConfig.USE_STUB) StubCityRepository(get()) else CityRepositoryImpl(get())
    }
    single { StubVisitRepository() }
    single<VisitRepository> {
        if (BuildConfig.DEBUG && BuildConfig.USE_STUB) get<StubVisitRepository>() else SupabaseVisitRepository(get())
    }
    factory { GetCountriesUseCase(get()) }
    factory { GetCitiesUseCase(get()) }
    factory { GetVisitedCitiesUseCase(get()) }
    factory { GetVisitsByCityUseCase(get()) }
    factory { GetVisitHistoryUseCase(get()) }
    factory { SaveVisitUseCase(get()) }
    factory { DeleteVisitUseCase(get()) }
    viewModel { CountryViewModel(get(), get()) }
    viewModel { CityViewModel(get(), get()) }
    viewModel { VisitViewModel(get(), get(), get(), get()) }
}
