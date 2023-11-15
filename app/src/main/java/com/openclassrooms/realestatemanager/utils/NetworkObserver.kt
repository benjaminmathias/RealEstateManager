package com.openclassrooms.realestatemanager.utils

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {

    // TODO("injecter directement sur activity/frag, bypass vm")
    val isConnected: Flow<Boolean>
}