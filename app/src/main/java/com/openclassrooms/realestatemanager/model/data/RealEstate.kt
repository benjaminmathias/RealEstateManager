package com.openclassrooms.realestatemanager.model.data

data class RealEstate(
    val type: String,
    val surface: Int,
    val price: Int,
    val description: String,
    val address: String,
    val nearbyPOI: List<String>,
    val isAvailable: Boolean,
    val photos: List<RealEstatePhoto>?,
    val entryDate: String,
    val saleDate: String?,
    val assignedAgent: String,
    val room: Int,
    val bedroom: Int,
    val bathroom: Int,
    val id: Long?,
    val lat: Double,
    val lon: Double
)