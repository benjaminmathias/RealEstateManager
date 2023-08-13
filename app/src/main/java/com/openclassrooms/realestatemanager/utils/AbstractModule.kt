package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.data.DefaultRealEstateRepository
import com.openclassrooms.realestatemanager.data.RealEstateRepository
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
}