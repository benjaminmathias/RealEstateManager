package com.openclassrooms.realestatemanager.data

data class RealEstate(
    val type: String,
    val surface: Int,
    val price: Int,
    val description: String,
    val address: String,
    // TODO : add nearbyPOI
    val nearbyPOI: ArrayList<String>,
    val isAvailable: Boolean,
    val photos: List<PhotoItem>?,
    val entryDate: String,
    val saleDate: String?,
    val assignedAgent: String,
    val room: Int,
    val bedroom: Int,
    val bathroom: Int,
    val id: Long?
)