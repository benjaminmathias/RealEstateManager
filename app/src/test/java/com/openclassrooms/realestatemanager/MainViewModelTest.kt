package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.model.repo.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.LocationService
import com.openclassrooms.realestatemanager.utils.NetworkObserver
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    private val realEstateRepository = Mockito.mock(RealEstateRepository::class.java)
    private val locationService = Mockito.mock(LocationService::class.java)
    private val networkObserver = Mockito.mock(NetworkObserver::class.java)

    private val newDate = OffsetDateTime.now()

    @Before
    fun setup(){
        viewModel = MainViewModel(realEstateRepository, locationService, networkObserver)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

}