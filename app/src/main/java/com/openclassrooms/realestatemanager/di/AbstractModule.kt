package com.openclassrooms.realestatemanager.di

import com.openclassrooms.realestatemanager.data.repository.DefaultRealEstateRepository
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.location.DefaultLocationService
import com.openclassrooms.realestatemanager.utils.location.LocationService
import com.openclassrooms.realestatemanager.utils.network.DefaultNetworkObserver
import com.openclassrooms.realestatemanager.utils.network.NetworkObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractModule {

    @Singleton
    @Binds
    abstract fun provideRealEstateRepository(
        defaultRealEstateRepository: DefaultRealEstateRepository
    ) : RealEstateRepository


    @Singleton
    @Binds
    abstract fun provideLocationService(
        defaultLocationService: DefaultLocationService
    ) : LocationService

    @Singleton
    @Binds
    abstract fun provideNetworkObserver(
        defaultNetworkObserver: DefaultNetworkObserver
    ) : NetworkObserver

}