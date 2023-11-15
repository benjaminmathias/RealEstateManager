package com.openclassrooms.realestatemanager.model.data

import java.time.OffsetDateTime

data class Filters(
    var type: String? = null,
    var priceMin: String? = null,
    var priceMax: String? = null,
    var surfaceMin: String? = null,
    var surfaceMax: String? = null,
    var location: String? = null,
    var nearbyPOI: List<String>? = null,
    var fromListedDate: OffsetDateTime? = null,
    var toListedDate: OffsetDateTime? = null,
    var fromSoldDate: OffsetDateTime? = null,
    var toSoldDate: OffsetDateTime? = null,
    var isAvailable: Boolean? = null
)