package com.openclassrooms.realestatemanager.model.data

data class Filters(
    val type: String? = null,
    val priceMin: String? = null,
    val priceMax: String? = null,
    val surfaceMin: String? = null,
    val surfaceMax: String? = null,
    val room: String? = null,
    val bedroom: String? = null,
    val bathroom: String? = null,
    val location: String? = null,
    val nearbyPOI: List<String>? = null
)