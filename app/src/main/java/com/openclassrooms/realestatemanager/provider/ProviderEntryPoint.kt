package com.openclassrooms.realestatemanager.provider

import com.openclassrooms.realestatemanager.data.db.RealEstateDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProviderEntryPoint {
    fun getDatabase() : RealEstateDatabase
}