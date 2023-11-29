package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.data.db.RealEstateDatabase
import com.openclassrooms.realestatemanager.data.db.dao.RealEstateDao
import com.openclassrooms.realestatemanager.data.db.entities.RealEstateEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Named
import kotlin.test.assertEquals

@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class RealEstateContentProviderTest {

    private var mContentResolver: ContentResolver? = null

    private val REALESTATE_ID: Long = 1
    private val authority = "com.openclassrooms.realestatemanager.provider"
    private val tableName: String = "realEstate"
    private val uri: Uri = Uri.parse("content://$authority/$tableName")

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: RealEstateDatabase
    private lateinit var realEstateDao: RealEstateDao

    private val realEstateEntityTest1 = RealEstateEntity(
        type = "HOUSE",
        surface = 250,
        price = 250,
        description = "House",
        address = "here",
        isAvailable = true,
        nearbyPOI = arrayListOf("RESTAURANT", "PARK"),
        entryDate = OffsetDateTime.now(),
        saleDate = null,
        assignedAgent = "JACK",
        room = 1,
        bedroom = 1,
        bathroom = 1,
        lat = 0.0,
        lon = 0.0,
        id = 1
    )

    @Before
    fun setup() {
        hiltRule.inject()
        realEstateDao = database.realEstateDao()
        mContentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getItemsWhenNoItemInserted() = runTest {
        val cursor = realEstateDao.getAllWithCursor()

        assertEquals(cursor.count, 0)
        cursor.close()
    }

    @Test
    fun insertAndGetItem() = runTest {
        // Adding a new RealEstate
        realEstateDao.insert(realEstateEntityTest1)

        val cursor = realEstateDao.getAllWithCursor()

        assertEquals(cursor.count, 1)
        cursor.close()
    }


}