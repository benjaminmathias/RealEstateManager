package com.openclassrooms.realestatemanager.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "realEstatePhoto"
)
data class RealEstatePhotoEntity(
    val uri: String,
    val photoDescription: String,
    val realEstateId: Long,
    @PrimaryKey(autoGenerate = false)
    val id: Long? = null
)