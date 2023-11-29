package com.openclassrooms.realestatemanager.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.db.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.db.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.data.db.entities.RealEstateEntity
import com.openclassrooms.realestatemanager.data.db.entities.RealEstatePhotoEntity
import com.openclassrooms.realestatemanager.data.model.Filters
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.RealEstatePhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.annotation.Config
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class DefaultRealEstateRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val realEstateDaoMock: RealEstateDao = mock(RealEstateDao::class.java)
    private val realEstatePhotoDao: RealEstatePhotoDao =
        mock(RealEstatePhotoDao::class.java)
    private val dispatcher = TestCoroutineDispatcher()

    private lateinit var defaultRealEstateRepository: DefaultRealEstateRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        defaultRealEstateRepository =
            DefaultRealEstateRepository(realEstateDaoMock, realEstatePhotoDao, dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun retrieveAndConvertShouldReturnList() = runTest {
        Mockito.`when`(realEstateDaoMock.getAll()).thenReturn(entityListFlow)

        val retrievedList = defaultRealEstateRepository.retrieveAndConvertRealEstateEntityList()

        Mockito.verify(realEstateDaoMock).getAll()

        assertEquals(2, retrievedList.first().size)
    }

    @Test
    fun retrieveAndConvertShouldReturnEmptyListWhenDbEmpty() = runTest {
        Mockito.`when`(realEstateDaoMock.getAll()).thenReturn(flowOf(emptyList()))

        val retrievedList = defaultRealEstateRepository.retrieveAndConvertRealEstateEntityList()

        assertEquals(0, retrievedList.first().size)
    }


    @Test
    fun retrieveAndConvertSpecificShouldReturnSingleItem() = runTest {
        Mockito.`when`(realEstatePhotoDao.getById(1)).thenReturn(photoEntityList)
        Mockito.`when`(defaultRealEstateRepository.fetchAllPhotoItem(1)).thenReturn(emptyList())
        Mockito.`when`(realEstateDaoMock.getById(1)).thenReturn(entityFlow)

        val retrievedItem =
            defaultRealEstateRepository.retrieveAndConvertSpecificRealEstateEntity(1)

        assertEquals("HOUSE", retrievedItem.first().type)
    }


    @Test
    fun retrieveAndConvertFilteredShouldReturnEmptyFilteredItems() = runTest {
        val filters = Filters(
            type = "HOUSE"
        )

        val query = defaultRealEstateRepository.buildQuery(filters)

        Mockito.`when`(realEstateDaoMock.getAllFiltered(query)).thenReturn(emptyList())
        Mockito.`when`(realEstatePhotoDao.getById(Mockito.anyLong())).thenReturn(emptyList())
        Mockito.`when`(defaultRealEstateRepository.fetchAllPhotoItem(Mockito.anyLong())).thenReturn(
            emptyList()
        )

        val filteredItem =
            defaultRealEstateRepository.retrieveAndConvertFilteredRealEstateList(filters)

        assertEquals(0, filteredItem.size)
    }

    @Test
    fun retrieveAndConvertFilteredShouldReturnFilteredItems() = runTest {
        val filters = Filters(
            type = "HOUSE"
        )

        val query = SimpleSQLiteQuery("")
        Mockito.`when`(realEstateDaoMock.getAllFiltered(query)).thenReturn(entityList)
        Mockito.`when`(realEstatePhotoDao.getById(1)).thenReturn(photoEntityList)

        val filteredItem: List<RealEstate> =
            defaultRealEstateRepository.retrieveAndConvertFilteredRealEstateList(filters)

        assertEquals(1, filteredItem.size)
    }

    @Test
    fun fetchAllPhotoItemShouldReturnItemsMatchingId() = runTest {
        Mockito.`when`(realEstatePhotoDao.getById(1)).thenReturn(photoEntityList)

        val photos = defaultRealEstateRepository.fetchAllPhotoItem(1)

        assertEquals(1, photos.size)
    }

    @Test
    fun fetchAllPhotoItemShouldReturnNoItemWhenNoMatchingId() = runTest {
        Mockito.`when`(realEstatePhotoDao.getById(5)).thenReturn(emptyList())

        val photos = defaultRealEstateRepository.fetchAllPhotoItem(5)

        assertEquals(0, photos.size)
    }

    @Test
    fun setRealEstateAsNoLongerAvailableShouldReturnNewValue() = runTest {
        realEstateDaoMock.insert(entity1)

        defaultRealEstateRepository.setRealEstateAsNoLongerAvailable(OffsetDateTime.now(), false, 1)

        val modifiedValues = realEstateDaoMock.getById(1)

        assertNotEquals(entity1.isAvailable, modifiedValues.first().isAvailable)
    }


    @Test
    fun buildQueryShouldReturnQueryWhenFiltersAreNotNull() = runTest {
        val filters = Filters(
            priceMin = "100000",
            priceMax = "500000"
        )

        val query = defaultRealEstateRepository.buildQuery(filters)

        assertEquals(
            "SELECT * FROM realEstate WHERE price >= 100000 AND price <= 500000",
            query.sql
        )
    }

    @Test
    fun buildQueryShouldReturnEmptyQueryWhenFiltersAreNull() = runTest {
        val filters = Filters()

        val query = defaultRealEstateRepository.buildQuery(filters)

        assertEquals("SELECT * FROM realEstate WHERE ", query.sql)
    }

    @Test
    fun insertRealEstateAndPhotoShouldReturnListWith1Item() = runTest {
        Mockito.`when`(realEstateDaoMock.getAll()).thenReturn(flowOf(emptyList()))

        val initialList = defaultRealEstateRepository.retrieveAndConvertRealEstateEntityList()

        assertEquals(0, initialList.first().size)

        // defaultRealEstateRepository.insertRealEstateAndPhoto(realEstate)

        Mockito.`when`(realEstateDaoMock.getAll()).thenReturn(flowOf(listOf(entity1)))

        val finalList = defaultRealEstateRepository.retrieveAndConvertRealEstateEntityList()

        assertEquals(1, finalList.first().size)
    }

    @Test
    fun setAsNoLongerAvailableShouldReturnItemWithNewBooleanValue() = runTest {
        Mockito.`when`(realEstatePhotoDao.getById(1)).thenReturn(photoEntityList)
        Mockito.`when`(defaultRealEstateRepository.fetchAllPhotoItem(1)).thenReturn(emptyList())
        Mockito.`when`(realEstateDaoMock.getById(1)).thenReturn(entityFlow)

        val retrievedItem =
            defaultRealEstateRepository.retrieveAndConvertSpecificRealEstateEntity(1)

        assertEquals(true, retrievedItem.first().isAvailable)

        defaultRealEstateRepository.setRealEstateAsNoLongerAvailable(OffsetDateTime.now(), false, 1)

        Mockito.`when`(realEstateDaoMock.getById(1)).thenReturn(flowOf(entity1Sold))

        val retrievedItemSold =
            defaultRealEstateRepository.retrieveAndConvertSpecificRealEstateEntity(1)

        assertEquals(false, retrievedItemSold.first().isAvailable)
    }

    @Test
    fun editRealEstateShouldReturnModifiedEstate() = runTest {
        Mockito.`when`(realEstatePhotoDao.getById(1)).thenReturn(photoEntityList)
        Mockito.`when`(defaultRealEstateRepository.fetchAllPhotoItem(1)).thenReturn(emptyList())
        Mockito.`when`(realEstateDaoMock.getById(1)).thenReturn(entityFlow)

        val retrievedItem =
            defaultRealEstateRepository.retrieveAndConvertSpecificRealEstateEntity(1)

        assertEquals("HOUSE", retrievedItem.first().type)

        defaultRealEstateRepository.editRealEstate(realEstate)

        Mockito.`when`(realEstateDaoMock.getById(1)).thenReturn(flowOf(entity1Modified))

        val retrieveEditedRealEstate = defaultRealEstateRepository.retrieveAndConvertSpecificRealEstateEntity(1)

        assertEquals("APARTMENT", retrieveEditedRealEstate.first().type)
        assertNotEquals(retrievedItem.first().type, retrieveEditedRealEstate.first().type)
    }
    companion object {

        private val date: OffsetDateTime = OffsetDateTime.now()

        private val realEstate = RealEstate(
            type = "APARTMENT",
            surface = 250,
            price = 350000,
            description = "yes",
            address = "france",
            isAvailable = true,
            nearbyPOI = arrayListOf("HEALTHCARE"),
            entryDate = date,
            saleDate = null,
            assignedAgent = "JACK",
            room = 8,
            bedroom = 5,
            bathroom = 3,
            lat = 1.1,
            lon = 1.1,
            id = null,
            photos = listOf()
        )

        private val entity1 = RealEstateEntity(
            type = "HOUSE",
            surface = 250,
            price = 350000,
            description = "yes",
            address = "france",
            isAvailable = true,
            nearbyPOI = null,
            entryDate = date,
            saleDate = null,
            assignedAgent = "JACK",
            room = 8,
            bedroom = 5,
            bathroom = 3,
            lat = 1.1,
            lon = 1.1,
            id = 1
        )

        private val entity1Sold = entity1.copy(
            isAvailable = false,
            saleDate = OffsetDateTime.now()
        )

        private val entity1Modified = entity1.copy(
            type = "APARTMENT"
        )

        private val entity2 = entity1.copy(
            type = "APARTMENT",
            id = 2
        )

        private val photoEntity1 = RealEstatePhotoEntity(
            "content://media/picker/0/com.android.providers.media.photopicker/media/1000000040",
            "test",
            1
        )

        private val photoEntity2 = photoEntity1.copy(
            id = 2
        )

        private val photoTest1 = RealEstatePhoto(
            "content://media/picker/0/com.android.providers.media.photopicker/media/1000000040",
            "test",
        )

        private val photoTest2 = photoTest1.copy()

        val entityListFlow = flowOf(listOf(entity1, entity2))
        val entityList = listOf(entity1)
        val entityFlow = flowOf(entity1)
        val photoEntityList = listOf(photoEntity1)
        val photoList = listOf(photoTest1, photoTest2)
    }
}