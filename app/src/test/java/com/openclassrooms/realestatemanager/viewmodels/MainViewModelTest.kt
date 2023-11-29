package com.openclassrooms.realestatemanager.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Filters
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.location.LocationService
import com.openclassrooms.realestatemanager.utils.network.NetworkObserver
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(manifest= Config.NONE)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    private val locationService = Mockito.mock(LocationService::class.java)
    private val networkObserver = Mockito.mock(NetworkObserver::class.java)
    private val realEstateRepositoryMock = Mockito.mock(RealEstateRepository::class.java)

    private val newDate = OffsetDateTime.now()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(realEstateRepositoryMock, locationService, networkObserver)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Get List
    // Non-filtered
    @Test
    fun getNonFilteredListShouldReturnListWhenListExist() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertRealEstateEntityList())
            .thenReturn(realEstateListFlow)

        viewModel.getNonFilteredList()

        assertTrue(viewModel.uiState.value is MainViewModel.MainUiState.Success)
        val uiState = viewModel.uiState.value as MainViewModel.MainUiState.Success
        assertEquals(2, uiState.realEstateList.size)

        assertFalse(viewModel.isFiltered.value)
    }

    @Test
    fun getNonFilteredListShouldReturnErrorWhenListNull() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertRealEstateEntityList())
            .thenThrow(Error("error"))

        viewModel.getNonFilteredList()

        assertTrue(viewModel.uiState.value is MainViewModel.MainUiState.Error)
        assertFalse(viewModel.uiState.value is MainViewModel.MainUiState.Success)

        assertFalse(viewModel.isFiltered.value)
    }

    // Filtered
    @Test
    fun filterListShouldReturnFilteredListWhenFiltersNotNullAndRetrieveSuccess() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertFilteredRealEstateList(Filters(type = "HOUSE")))
            .thenReturn(realEstateFilterList)

        val id: Int = R.id.first_type_chip
        viewModel.checkedTypeChipObservable.value = listOf(id)

        viewModel.filterList()

        assertFalse(viewModel.noQuery.value)
        assertTrue(viewModel.uiState.value is MainViewModel.MainUiState.Success)
        val uiState = viewModel.uiState.value as MainViewModel.MainUiState.Success
        assertEquals(1, uiState.realEstateList.size)
        assertEquals("HOUSE", uiState.realEstateList[0].type)
    }

    @Test
    fun filterListShouldReturnNoQueryWhenFiltersAreNull() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertFilteredRealEstateList(Filters()))
            .thenReturn(emptyList())

        viewModel.filterList()

        assertTrue(viewModel.noQuery.value)
    }

    // Filters
    // Type
    @Test
    fun getSelectedRealEstateTypeShouldReturnOneItemWhenSuccess() = runTest {
        val id: Int = R.id.first_type_chip
        viewModel.checkedTypeChipObservable.value = listOf(id)
        val retrievedType = viewModel.getSelectedRealEstateType()
        assertEquals(retrievedType?.get(0), "HOUSE")
    }

    @Test
    fun getSelectedRealEstateTypeShouldReturnNullWhenEmpty() = runTest {
        viewModel.checkedTypeChipObservable.value = emptyList()
        val type = viewModel.getSelectedRealEstateType()
        assertEquals(type?.get(0), null)
    }

    // POI
    @Test
    fun getSelectedRealEstatePoiShouldReturnItemsWhenSuccess() = runTest {
        val id: Int = R.id.healthcare_poi_chip
        viewModel.checkedPoiChipObservable.value = listOf(id)
        val poi = viewModel.getSelectedRealEstatePoi()
        assertEquals(poi?.get(0), "HEALTHCARE")
    }

    @Test
    fun getSelectedRealEstatePoiShouldNullWhenEmpty() = runTest {
        viewModel.checkedPoiChipObservable.value = emptyList()
        val poi = viewModel.getSelectedRealEstatePoi()
        assertEquals(null, poi)
    }

    // --
    // Availability
    @Test
    fun getSelectedAvailabilityShouldReturnBooleanValueWhenSelected() = runTest {
        val id: Int = R.id.radio_yes
        viewModel.checkedAvailableRadioButtonObservable.value = id
        assertEquals(viewModel.getSelectedAvailability(), true)
    }

    @Test
    fun getSelectedAvailabilityShouldReturnNullWhenNotSelected() = runTest {
        viewModel.checkedAvailableRadioButtonObservable.value = 0
        val isAvailable = viewModel.getSelectedAvailability()
        assertEquals(null, isAvailable)
    }

    // Set Date tests
    // ListedDate
    @Test
    fun setToListedDateNewValueShouldReturnValue() = runTest {
        val emptyDate = viewModel.realEstateFilters.value.toListedDate
        viewModel.setListedDateTo(newDate)
        val newDateSet = viewModel.realEstateFilters.value.toListedDate

        assertNotEquals(emptyDate, newDateSet)
        assertEquals(newDate, newDateSet)
    }

    @Test
    fun setToListedDateNullShouldReturnNull() = runTest {
        viewModel.setListedDateTo(null)
        val newDate = viewModel.realEstateFilters.value.toListedDate

        assertNull(newDate)
    }

    @Test
    fun setFromListedDateNewValueShouldReturnValue() = runTest {
        val emptyDate = viewModel.realEstateFilters.value.fromListedDate
        viewModel.setListedDateFrom(newDate)
        val newDateSet = viewModel.realEstateFilters.value.fromListedDate

        assertNotEquals(emptyDate, newDateSet)
        assertEquals(newDate, newDateSet)
    }

    @Test
    fun setFromListedDateNullShouldReturnNull() = runTest {
        viewModel.setListedDateFrom(null)
        val newDate = viewModel.realEstateFilters.value.fromListedDate

        assertNull(newDate)
    }


    // SoldDate
    @Test
    fun setToSoldDateNewValueShouldReturnValue() = runTest {
        val emptyDate = viewModel.realEstateFilters.value.toSoldDate
        viewModel.setSoldDateTo(newDate)
        val newDateSet = viewModel.realEstateFilters.value.toSoldDate

        assertNotEquals(emptyDate, newDateSet)
        assertEquals(newDate, newDateSet)
    }

    @Test
    fun setToSoldDateToNullShouldReturnNull() = runTest {
        viewModel.setSoldDateTo(null)
        val newDate = viewModel.realEstateFilters.value.toSoldDate

        assertNull(newDate)
    }

    @Test
    fun setFromSoldDateNewValueShouldReturnValue() = runTest {
        val emptyDate = viewModel.realEstateFilters.value.fromSoldDate
        viewModel.setSoldDateFrom(newDate)
        val newDateSet = viewModel.realEstateFilters.value.fromSoldDate

        assertNotEquals(emptyDate, newDateSet)
        assertEquals(newDate, newDateSet)
    }

    @Test
    fun setFromSoldDateToNullShouldReturnNull() = runTest {
        viewModel.setSoldDateFrom(null)
        val newDate = viewModel.realEstateFilters.value.fromSoldDate

        assertNull(newDate)
    }

    companion object {
        private val firstRe = RealEstate(
            type = "HOUSE",
            surface = 250,
            price = 1500000,
            description = "House",
            address = "France",
            nearbyPOI = null,
            isAvailable = true,
            photos = null,
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

        private val secondRe = firstRe.copy(
            type = "APARTMENT",
            id = 2
        )

        private val realEstateListFlow = flowOf(listOf(firstRe, secondRe))
        private val realEstateFilterList = listOf(firstRe)
    }
}