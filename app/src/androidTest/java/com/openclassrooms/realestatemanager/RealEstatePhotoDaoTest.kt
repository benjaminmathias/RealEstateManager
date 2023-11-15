package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.openclassrooms.realestatemanager.model.data.RealEstateDatabase
import com.openclassrooms.realestatemanager.model.data.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.model.data.RealEstatePhotoEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class RealEstatePhotoDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: RealEstateDatabase
    private lateinit var realEstatePhotoDao: RealEstatePhotoDao

    private val photoEntityTest1 = RealEstatePhotoEntity(
        "content://media/picker/0/com.android.providers.media.photopicker/media/1000000040",
        "test",
        1
    )

    private val photoEntityTest1Update = RealEstatePhotoEntity(
        "content://media/picker/0/com.android.providers.media.photopicker/media/1000000040",
        "test update",
        1
    )

    private val photoEntityTest2 = RealEstatePhotoEntity(
        "content://media/picker/0/com.android.providers.media.photopicker/media/1000000040",
        "test1",
        2
    )

    @Before
    fun setup() {
        hiltRule.inject()
        realEstatePhotoDao = database.photoItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetPhotoById() = runTest {
        // Adding new photo entity
        realEstatePhotoDao.insert(photoEntityTest1)

        // Get photo by id
        val photo = realEstatePhotoDao.getById(1)[0]

        // Assert that our retrieved photo match the added one
        assertEquals(photo.photoDescription, photoEntityTest1.photoDescription)
    }

    @Test
    fun updateExistingPhoto() = runTest {
        // Adding new photo entity
        realEstatePhotoDao.insert(photoEntityTest1)

        // Get photo by id
        val photo = realEstatePhotoDao.getById(1)[0]

        // To update our photo, we delete the existing list with matching ids
        realEstatePhotoDao.deletePhotosByRealEstateId(1)

        // Then we insert the new one
        realEstatePhotoDao.insert(photoEntityTest1Update)

        // Get updated photo
        val updatedPhoto = realEstatePhotoDao.getById(1)[0]

        // Assert that our updated element differs from original one
        assertNotEquals(photo.photoDescription, updatedPhoto.photoDescription)
    }


}