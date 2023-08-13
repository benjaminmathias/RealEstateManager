package com.openclassrooms.realestatemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "realEstate")
data class RealEstateEntity(
    val type: String,
    val surface: Int,
    val price: Int,
    val description: String,
    val address: String,
    val nearbyPOI: ArrayList<String>,
    val isAvailable: Boolean,
    val entryDate: String,
    val saleDate: String?,
    val assignedAgent: String,
    val room: Int,
    val bedroom: Int,
    val bathroom: Int,
    val lat: Double,
    val lon: Double,
    @PrimaryKey(autoGenerate = false)
    val id: Long? = null
)
