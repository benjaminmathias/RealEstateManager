package com.openclassrooms.realestatemanager.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.data.model.UserLocation
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.location.LocationService
import com.openclassrooms.realestatemanager.viewmodel.NewEditRealEstateViewModel
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
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class NewEditRealEstateViewModelTest {

    private lateinit var viewModel: NewEditRealEstateViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    private val savedStateHandle = SavedStateHandle()
    private val locationService = Mockito.mock(LocationService::class.java)
    private val realEstateRepositoryMock = Mockito.mock(RealEstateRepository::class.java)

    private val newDate = OffsetDateTime.now()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel =
            NewEditRealEstateViewModel(realEstateRepositoryMock, locationService, savedStateHandle)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun retrieveStoredRealEstateShouldReturnItem() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertSpecificRealEstateEntity(1))
            .thenReturn(realEstateStoredFlow)

        viewModel.retrieveStoredRealEstate(1)

        assertEquals(
            viewModel.realEstateDataFlow.value.address,
            realEstateStoredFlow.first().address
        )
    }

    @Test
    fun updateEntryDateShouldReturnNewValue() = runTest {
        val emptyDate = viewModel.realEstateDataFlow.value.entryDate
        viewModel.updateEntryDate(newDate)
        val newDateSet = viewModel.realEstateDataFlow.value.entryDate

        assertNotEquals(emptyDate, newDateSet)
        assertEquals(newDate, newDateSet)
    }

    @Test
    fun getCurrentLocationShouldReturnValueWhenSuccess() = runTest {
        Mockito.`when`(locationService.userLocation).thenReturn(locationFlow)

        viewModel.getCurrentLocation()

        assertEquals("HERE", viewModel.realEstateDataFlow.value.address)
    }

    @Test
    fun clearExistingLocationShouldReturnNullValuesWhenSuccess() = runTest {
        Mockito.`when`(locationService.userLocation).thenReturn(locationFlow)

        viewModel.getCurrentLocation()

        assertEquals("HERE", viewModel.realEstateDataFlow.value.address)

        viewModel.clearExistingLocation()

        assertEquals(null, viewModel.realEstateDataFlow.value.address)
    }


    // CHIP TEST
    // Type
    @Test
    fun getSelectedRealEstateTypeShouldReturnOneItemWhenSuccess() = runTest {
        val id: Int = R.id.first_type_chip
        viewModel.checkedTypeChipObservable.value = listOf(id)
        val retrievedType = viewModel.getSelectedRealEstateType()
        assertEquals(retrievedType[0], "HOUSE")
    }

    @Test
    fun getSelectedRealEstateTypeShouldReturnNullWhenEmpty() = runTest {
        viewModel.checkedTypeChipObservable.value = emptyList()
        val type = viewModel.getSelectedRealEstateType()
        assertEquals(emptyList(), type)
    }

    // POI
    @Test
    fun getSelectedRealEstatePoiShouldReturnItemsWhenSuccess() = runTest {
        val id: Int = R.id.healthcare_poi_chip
        viewModel.checkedPoiChipObservable.value = listOf(id)
        val poi = viewModel.getSelectedRealEstatePoi()
        assertEquals(poi[0], "HEALTHCARE")
    }

    @Test
    fun getSelectedRealEstatePoiShouldNullWhenEmpty() = runTest {
        viewModel.checkedPoiChipObservable.value = emptyList()
        val poi = viewModel.getSelectedRealEstatePoi()
        assertEquals(emptyList(), poi)
    }

    // Agent
    @Test
    fun getSelectedAgentShouldReturnAgentValueWhenSelected() = runTest {
        val id: Int = R.id.first_agent_chip
        viewModel.checkedAgentChipObservable.value = listOf(id)
        assertEquals(viewModel.getSelectedRealEstateAgent(), listOf("JACK"))
    }

    @Test
    fun getSelectedAgentShouldReturnNullWhenNotSelected() = runTest {
        viewModel.checkedAgentChipObservable.value = emptyList()
        val nullAgent = viewModel.getSelectedRealEstateAgent()
        assertEquals(emptyList(), nullAgent)
    }

    companion object {
        private val location = UserLocation(1.1, 1.1, "HERE")
        val locationFlow = flowOf(location)

        private val photo = RealEstatePhoto(
            "123",
            "123"
        )

        private val photoList = mutableListOf(photo)

        private val poiList = listOf("HEALTHCARE")

        private val stored = RealEstate(
            type = "HOUSE",
            surface = 250,
            price = 1500000,
            description = "House",
            address = "France",
            nearbyPOI = poiList,
            isAvailable = true,
            photos = photoList,
            entryDate = OffsetDateTime.now(),
            saleDate = null,
            assignedAgent = "JACK",
            room = 8,
            bedroom = 5,
            bathroom = 2,
            id = 1,
            lat = 1.1,
            lon = 1.1
        )

        private val realEstateStoredFlow = flowOf(stored)


    }
}