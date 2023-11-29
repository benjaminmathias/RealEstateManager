package com.openclassrooms.realestatemanager.data.mapper

import com.openclassrooms.realestatemanager.data.db.entities.RealEstateEntity
import com.openclassrooms.realestatemanager.data.db.entities.RealEstatePhotoEntity
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.RealEstatePhoto

class RealEstateMapper {
    fun convertRealEstateToEntity(realEstate: RealEstate): RealEstateEntity {
        return RealEstateEntity(
            type = realEstate.type,
            surface = realEstate.surface,
            price = realEstate.price,
            description = realEstate.description,
            address = realEstate.address,
            isAvailable = realEstate.isAvailable,
            nearbyPOI = realEstate.nearbyPOI as ArrayList<String>,
            entryDate = realEstate.entryDate,
            saleDate = realEstate.saleDate,
            assignedAgent = realEstate.assignedAgent,
            room = realEstate.room,
            bedroom = realEstate.bedroom,
            bathroom = realEstate.bathroom,
            lat = realEstate.lat,
            lon = realEstate.lon,
            id = realEstate.id
        )
    }

    fun convertEntityToRealEstate(
        realEstateEntity: RealEstateEntity,
        photos: List<RealEstatePhoto>,
    ): RealEstate {
        return RealEstate(
            type = realEstateEntity.type,
            surface = realEstateEntity.surface,
            price = realEstateEntity.price,
            description = realEstateEntity.description,
            address = realEstateEntity.address,
            nearbyPOI = realEstateEntity.nearbyPOI,
            isAvailable = realEstateEntity.isAvailable,
            photos = photos,
            entryDate = realEstateEntity.entryDate,
            saleDate = realEstateEntity.saleDate,
            assignedAgent = realEstateEntity.assignedAgent,
            room = realEstateEntity.room,
            bedroom = realEstateEntity.bedroom,
            bathroom = realEstateEntity.bathroom,
            lat = realEstateEntity.lat,
            lon = realEstateEntity.lon,
            id = realEstateEntity.id
        )
    }

    fun convertPhotoEntityToModel(entity : RealEstatePhotoEntity) : RealEstatePhoto {
        return RealEstatePhoto(
            uri = entity.uri,
            photoDescription = entity.photoDescription
        )
    }
}