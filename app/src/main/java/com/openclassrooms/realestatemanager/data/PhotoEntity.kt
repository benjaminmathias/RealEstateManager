package com.openclassrooms.realestatemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "photoItemTable"
)
data class PhotoEntity(
    val uri: String,
    val photoDescription: String,
    val realEstateId: Long,
    @PrimaryKey(autoGenerate = false)
    val photoGeneratedId: Long? = null
)