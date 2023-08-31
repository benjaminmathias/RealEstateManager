package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.model.data.RealEstateDao
import com.openclassrooms.realestatemanager.model.data.RealEstateDatabase
import com.openclassrooms.realestatemanager.model.data.RealEstateEntity
import com.openclassrooms.realestatemanager.model.data.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.model.data.RealEstatePhotoEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class RealEstateEntityDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: RealEstateDatabase
    private lateinit var realEstateDao: RealEstateDao
    private lateinit var realEstatePhotoDao: RealEstatePhotoDao

    @Before
    fun setup() {
        hiltRule.inject()
        realEstateDao = database.realEstateDao()
        realEstatePhotoDao = database.photoItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertRealEstate() = runBlockingTest {

        val realEstateEntity = RealEstateEntity(
            type = "HOUSE",
            surface = 250,
            price = 250,
            description = "House",
            address = "here",
            isAvailable = true,
            nearbyPOI = arrayListOf("RESTAURANT","PARK"),
            entryDate = "17/07/2023",
            saleDate = null,
            assignedAgent = "JACK",
            room = 1,
            bedroom = 1,
            bathroom = 1,
            lat = 0.0,
            lon = 0.0
        )

        val insertedId: Long = realEstateDao.insert(realEstateEntity)

        val photoToInsert = RealEstatePhotoEntity(
            "content://media/picker/0/com.android.providers.media.photopicker/media/1000000040",
            "test",
            insertedId
        )

        realEstatePhotoDao.insert(photoToInsert)
        val getRealEstate = realEstateDao.getAll().first()
        assertThat(getRealEstate).contains(realEstateEntity)
    }
}