package com.openclassrooms.realestatemanager.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.openclassrooms.realestatemanager.model.data.RealEstate
import com.openclassrooms.realestatemanager.model.data.RealEstateDao
import com.openclassrooms.realestatemanager.model.data.RealEstateEntity
import com.openclassrooms.realestatemanager.model.data.RealEstatePhoto
import com.openclassrooms.realestatemanager.model.data.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.model.repo.DefaultRealEstateRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@SmallTest
class DefaultRealEstateRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val realEstateDaoMock = Mockito.mock(RealEstateDao::class.java)
    val realEstatePhotoDao = Mockito.mock(RealEstatePhotoDao::class.java)

    private lateinit var defaultRealEstateRepository : DefaultRealEstateRepository

    @Before
    fun setup() {
       // defaultRealEstateRepository = DefaultRealEstateRepository(realEstateDaoMock, realEstatePhotoDao)
    }

    val entityToBeConverted = RealEstateEntity(
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

    val realEstateToBeConverted = RealEstate(
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



    @Test
    fun retrieveAndConvertShouldReturnList() = runTest {

    }

    @Test
    fun retrieveAndConvertSpecificShouldReturnSingleItem() = runTest {

    }

    @Test
    fun retrieveAndConvertFilteredShouldReturnFilteredItems() = runTest {

    }

    @Test
    fun fetchAllPhotoItemShouldReturnItemsMatchingId() {

    }

    @Test
    fun convertEntityToRealEstateShouldReturnSameElement() = runTest {

        val baseType = entityToBeConverted.type

        val convertedEstate = defaultRealEstateRepository.convertEntityToRealEstate(entityToBeConverted)

        assertEquals(baseType, convertedEstate.type)
    }

    @Test
    fun convertRealEstateToEntityShouldReturnSameElement() {

        val base = realEstateToBeConverted.type

        val convertedEstateToEntity = defaultRealEstateRepository.convertRealEstateToEntity(realEstateToBeConverted)

        assertEquals(base, convertedEstateToEntity.type)
    }
}