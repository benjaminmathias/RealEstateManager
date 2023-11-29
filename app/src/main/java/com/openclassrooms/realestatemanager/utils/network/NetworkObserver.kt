package com.openclassrooms.realestatemanager.utils.network

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {

    val isConnected: Flow<Boolean>
}