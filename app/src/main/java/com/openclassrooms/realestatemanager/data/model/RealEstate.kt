package com.openclassrooms.realestatemanager.data.model

import java.time.OffsetDateTime

data class RealEstate(
    val type: String,
    val surface: Int?,
    val price: Int,
    val description: String?,
    val address: String?,
    val nearbyPOI: List<String>?,
    val isAvailable: Boolean,
    val photos: List<RealEstatePhoto>?,
    val entryDate: OffsetDateTime?,
    val saleDate: OffsetDateTime?,
    val assignedAgent: String,
    val room: Int?,
    val bedroom: Int?,
    val bathroom: Int?,
    val id: Long?,
    val lat: Double?,
    val lon: Double?
)