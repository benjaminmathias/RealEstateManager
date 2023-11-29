package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.data.db.RealEstateDatabase
import com.openclassrooms.realestatemanager.data.db.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.db.entities.RealEstateEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Named
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class RealEstateDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: RealEstateDatabase
    private lateinit var realEstateDao: RealEstateDao
    private var cal : Calendar = Calendar.getInstance()

    private fun convertDateToOffsetDateTime(day: Int, month: Int, year: Int): OffsetDateTime {
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        val date = cal.time
        val zoneOffset = ZoneOffset.of("+02:00")
        return date.toInstant().atOffset(zoneOffset)
    }

    private val realEstateEntityTest1 = RealEstateEntity(
        type = "HOUSE",
        surface = 250,
        price = 250,
        description = "House",
        address = "here",
        isAvailable = true,
        nearbyPOI = arrayListOf("RESTAURANT", "PARK"),
        entryDate = convertDateToOffsetDateTime(5,5,2023),
        saleDate = null,
        assignedAgent = "JACK",
        room = 1,
        bedroom = 1,
        bathroom = 1,
        lat = 0.0,
        lon = 0.0,
        id = 1
    )

    private val realEstateEntityTest1Update = RealEstateEntity(
        type = "HOUSE",
        surface = 250,
        price = 250,
        description = "House",
        address = "here updated",
        isAvailable = true,
        nearbyPOI = arrayListOf("RESTAURANT", "PARK"),
        entryDate = convertDateToOffsetDateTime(5,5,2023),
        saleDate = null,
        assignedAgent = "JACK",
        room = 1,
        bedroom = 1,
        bathroom = 1,
        lat = 0.0,
        lon = 0.0,
        id = 1
    )

    private val realEstateEntityTest2 = RealEstateEntity(
        type = "APARTMENT",
        surface = 250,
        price = 250,
        description = "Apartment",
        address = "there",
        isAvailable = true,
        nearbyPOI = arrayListOf("RESTAURANT", "PARK"),
        entryDate = convertDateToOffsetDateTime(5,5,2023),
        saleDate = null,
        assignedAgent = "JACK",
        room = 2,
        bedroom = 2,
        bathroom = 2,
        lat = 0.0,
        lon = 0.0,
        id = 2
    )

    @Before
    fun setup() {
        hiltRule.inject()
        realEstateDao = database.realEstateDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetSingleRealEstate() = runTest {
        // Adding a new RealEstate
        realEstateDao.insert(realEstateEntityTest1)

        // Retrieving all RealEstate
        val getRealEstate = realEstateDao.getAll().first()

        // Checking that our list contain our added element
        assertEquals(getRealEstate[0].address, realEstateEntityTest1.address)
    }

    @Test
    fun insertMultipleAndGetAllRealEstate() = runTest {
        // Add 2 new RealEstateEntity
        realEstateDao.insert(realEstateEntityTest1)
        realEstateDao.insert(realEstateEntityTest2)

        // Retrieve all
        val getRealEstate = realEstateDao.getAll().first()

        // Assert that our retrieved list contains both added elements
        assertThat(getRealEstate.size == 2)
    }

    @Test
    fun getBySpecificId() = runTest {
        // Add 2 new RealEstateEntity
        realEstateDao.insert(realEstateEntityTest1)
        realEstateDao.insert(realEstateEntityTest2)

        // Retrieve RealEstate with id = 1
        val getSingleRealEstate = realEstateDao.getById(1).first()

        // Assert that our retrieved RealEstate is matching
        assertEquals(getSingleRealEstate.type, realEstateEntityTest1.type)
    }

    @Test
    fun getBySpecificIdFail() = runTest {
        // Add 2 new RealEstateEntity
        realEstateDao.insert(realEstateEntityTest1)
        realEstateDao.insert(realEstateEntityTest2)

        // Retrieve RealEstate with id = 1
        val getSingleRealEstate = realEstateDao.getById(1).first()

        // Assert that our retrieved RealEstate is not matching the second id
        assertNotEquals(getSingleRealEstate.type, realEstateEntityTest2.type)
    }

    @Test
    fun updatedRealEstateShouldReturnNewItem() = runTest {
        // Insert a single item
        realEstateDao.insert(realEstateEntityTest1)

        // Return a single item
        val getSingleRealEstate = realEstateDao.getById(1).first()

        // Assert that both address are matching
        assertEquals(realEstateEntityTest1.address, getSingleRealEstate.address)

        // Update
        realEstateDao.update(realEstateEntityTest1Update)

        // Retrieved the newly updated item
        val updatedRealEstate = realEstateDao.getById(1).first()

        // Assert that our first retrieved item address doesn't match the updated one
        assertNotEquals(updatedRealEstate.address, realEstateEntityTest1.address)
    }

    @Test
    fun updateRealEstateShouldChangeSaleDateAndAvailability() = runTest {
        // Insert a single item
        realEstateDao.insert(realEstateEntityTest1)

        val saleDateBaseValue = realEstateDao.getById(1).first().saleDate
        val isAvailableBaseValue = realEstateDao.getById(1).first().isAvailable

        // Mark the item as sold, changing its saleDate and isAvailable values
        realEstateDao.updateRealEstate(convertDateToOffsetDateTime(10,10,2023), false, 1)

        val soldRealEstate = realEstateDao.getById(1).first()

        // Assert that both new values are not equals to initial one
        assertNotEquals(saleDateBaseValue, soldRealEstate.saleDate)
        assertNotEquals(isAvailableBaseValue, soldRealEstate.isAvailable)
    }

    @Test
    fun getAllFilteredShouldReturnOnlyMatchingItems() = runTest {
        // Add 2 new RealEstateEntity
        realEstateDao.insert(realEstateEntityTest1)
        realEstateDao.insert(realEstateEntityTest2)

        // Build a query matching our first realEstate infos
        val query = SimpleSQLiteQuery(
            "SELECT * FROM realEstate WHERE type LIKE 'HOUSE' AND address LIKE '%here%'"
        )

        // Get filtered items
        val filteredItem = realEstateDao.getAllFiltered(query)[0]

        // Assert that our retrieved Filtered items is matching
        assertEquals(filteredItem.type, realEstateEntityTest1.type)
    }
}