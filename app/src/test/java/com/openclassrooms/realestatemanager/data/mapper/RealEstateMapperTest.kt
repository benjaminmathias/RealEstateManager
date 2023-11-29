package com.openclassrooms.realestatemanager.data.mapper

import com.openclassrooms.realestatemanager.data.db.entities.RealEstateEntity
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.RealEstatePhoto
import org.junit.Test
import kotlin.test.assertEquals

class RealEstateMapperTest {

    private val mapper = RealEstateMapper()
    @Test
    fun convertEntityToRealEstateShouldReturnSameElement() {

        val baseType = entityToBeConverted.type

        val convertedEstate = mapper.convertEntityToRealEstate(
            entityToBeConverted,
            listOf(photoTest1)
        )

        assertEquals(baseType, convertedEstate.type)
    }

    @Test
    fun convertRealEstateToEntityShouldReturnSameElement() {

        val base = realEstateToBeConverted.type

        val convertedEstateToEntity =
            mapper.convertRealEstateToEntity(realEstateToBeConverted)

        assertEquals(base, convertedEstateToEntity.type)
    }


    companion object {
        private val entityToBeConverted = RealEstateEntity(
            type = "HOUSE",
            surface = 250,
            price = 250,
            description = "House",
            address = "here",
            isAvailable = true,
            nearbyPOI = arrayListOf("RESTAURANT", "PARK"),
            entryDate = null,
            saleDate = null,
            assignedAgent = "JACK",
            room = 1,
            bedroom = 1,
            bathroom = 1,
            lat = 0.0,
            lon = 0.0,
            id = 1
        )

        private val photoTest1 = RealEstatePhoto(
            "content://media/picker/0/com.android.providers.media.photopicker/media/1000000040",
            "test",
        )

        private val realEstateToBeConverted = RealEstate(
            type = "HOUSE",
            surface = 250,
            price = 250,
            description = "House",
            address = "here",
            isAvailable = true,
            nearbyPOI = arrayListOf("RESTAURANT", "PARK"),
            entryDate = null,
            saleDate = null,
            assignedAgent = "JACK",
            room = 1,
            bedroom = 1,
            bathroom = 1,
            lat = 0.0,
            lon = 0.0,
            id = 1,
            photos = listOf(photoTest1)
        )
    }
}