package com.openclassrooms.realestatemanager.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "realEstate")
data class RealEstateEntity(
    var type: String,
    var surface: Int?,
    var price: Int,
    var description: String?,
    var address: String?,
    var nearbyPOI: ArrayList<String>?,
    var isAvailable: Boolean,
    var entryDate: OffsetDateTime?,
    var saleDate: OffsetDateTime?,
    var assignedAgent: String,
    var room: Int?,
    var bedroom: Int?,
    var bathroom: Int?,
    var lat: Double?,
    var lon: Double?,
    @PrimaryKey(autoGenerate = false)
    val id: Long? = null
)

