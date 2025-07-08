package com.studiomk.matool.di

import com.studiomk.matool.application.service.AuthService
import com.studiomk.matool.application.service.LocationService

import com.studiomk.matool.data.api.live.LiveApiRepository
import com.studiomk.matool.data.api.mock.MockApiRepository
import com.studiomk.matool.data.auth.aws_cognito.AwsCognitoProvider
import com.studiomk.matool.data.local_store.shared_preferences.SharedPreferencesLocalStore
import com.studiomk.matool.data.location.live.LiveLocationClient
import com.studiomk.matool.domain.contracts.api.*
import com.studiomk.matool.domain.contracts.auth.AuthProvider
import com.studiomk.matool.domain.contracts.local_store.LocalStore
import com.studiomk.matool.domain.contracts.location.LocationClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val liveAppModule = module {
    single<ApiRepository> { LiveApiRepository() }
    single<AuthService> { AuthService() }
    single<AuthProvider> { AwsCognitoProvider() }
    single<LocalStore> { SharedPreferencesLocalStore(androidContext()) }
    single<LocationService> { LocationService() }
    single<LocationClient> { LiveLocationClient(androidContext()) }
    single<DefaultValues> { DefaultValues }
}

val mockAppModule = module {
    single<ApiRepository> { MockApiRepository() }
    single<AuthService> { AuthService() }
}

