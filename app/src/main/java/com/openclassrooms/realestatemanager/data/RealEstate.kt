package com.openclassrooms.realestatemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="realEstateTable")
data class RealEstate (val name: String,
                       val price: Int,
                       val surface: Int,
                       @PrimaryKey(autoGenerate = false) val id: Int? = null)
