package com.openclassrooms.realestatemanager.model.data

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
    // var entryDate: String,
    // var entryDate: Date?,
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
){
   /* companion object {

        fun fromContentValues(values: ContentValues): RealEstateEntity {
            var realEstateEntity : RealEstateEntity =
                RealEstateEntity(
                    "",
                    0,
                    0,
                    "",
                    "",
                    emptyList<String>() as ArrayList,
                    true,
                    "",
                    "",
                    "",
                    0,
                    0,
                    0,
                    0.0,
                    0.0,
                    1)

            if (values.containsKey("type")) realEstateEntity.type = values.getAsString("type")
            if (values.containsKey("description")) realEstateEntity.description = values.getAsString("description")
            if (values.containsKey("address")) realEstateEntity.address = values.getAsString("address")
            if (values.containsKey("entryDate")) realEstateEntity.entryDate = values.getAsString("entryDate")
            if (values.containsKey("saleDate")) realEstateEntity.saleDate = values.getAsString("saleDate")
            if (values.containsKey("assignedAgent")) realEstateEntity.assignedAgent = values.getAsString("assignedAgent")
            if (values.containsKey("surface")) realEstateEntity.surface = values.getAsInteger("surface")
            if (values.containsKey("price")) realEstateEntity.price = values.getAsInteger("price")
            if (values.containsKey("room")) realEstateEntity.room = values.getAsInteger("room")
            if (values.containsKey("bedroom")) realEstateEntity.bedroom = values.getAsInteger("bedroom")
            if (values.containsKey("bathroom")) realEstateEntity.bathroom = values.getAsInteger("bathroom")
            if (values.containsKey("surface")) realEstateEntity.surface = values.getAsInteger("surface")
            if (values.containsKey("lat")) realEstateEntity.lat = values.getAsDouble("lat")
            if (values.containsKey("lon")) realEstateEntity.lon = values.getAsDouble("lon")

            return realEstateEntity
        }
    }*/
}

