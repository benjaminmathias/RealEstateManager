package com.openclassrooms.realestatemanager.di

import com.openclassrooms.realestatemanager.model.repo.DefaultRealEstateRepository
import com.openclassrooms.realestatemanager.model.repo.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.DefaultLocationService
import com.openclassrooms.realestatemanager.utils.LocationService
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