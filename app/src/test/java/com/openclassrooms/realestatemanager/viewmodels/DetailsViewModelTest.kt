package com.openclassrooms.realestatemanager.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.viewmodel.DetailsViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class DetailsViewModelTest {

    private lateinit var viewModel: DetailsViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    private val savedStateHandle = SavedStateHandle(mapOf<String, Long>("id" to 1))
    private val realEstateRepositoryMock = Mockito.mock(RealEstateRepository::class.java)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = DetailsViewModel(realEstateRepositoryMock, savedStateHandle)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getSpecificRealEstateConvertedShouldReturnItemWhenSuccess() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertSpecificRealEstateEntity(1))
            .thenReturn(realEstateDetailsFlow)

        viewModel.getSpecificRealEstateConverted(1)

        assertTrue(viewModel.detailsUiState.value is DetailsViewModel.DetailsUiState.Success)
        val detailsUiState =
            viewModel.detailsUiState.value as DetailsViewModel.DetailsUiState.Success
        assertEquals(1, detailsUiState.realEstateData.id)
        assertEquals(1, viewModel.realEstateDataDetailsFlow.value.id)
    }

    @Test
    fun getSpecificRealEstateConvertedShouldReturnErrorWhenIdNotExist() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertSpecificRealEstateEntity(0))
            .thenReturn(null)

        viewModel.getSpecificRealEstateConverted(0)

        assertTrue(viewModel.detailsUiState.value is DetailsViewModel.DetailsUiState.Error)
    }

    @Test
    fun setRealEstateAsNoLongerAvailableShouldReturnNewValue() = runTest {
        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertSpecificRealEstateEntity(1))
            .thenReturn(realEstateDetailsFlow)

        viewModel.getSpecificRealEstateConverted(1)

        assertTrue(viewModel.detailsUiState.value is DetailsViewModel.DetailsUiState.Success)
        val detailsUiState =
            viewModel.detailsUiState.value as DetailsViewModel.DetailsUiState.Success
        assertEquals(true, detailsUiState.realEstateData.isAvailable)

        viewModel.setRealEstateAsNoLongerAvailable(OffsetDateTime.now(), false, 1)

        Mockito.`when`(realEstateRepositoryMock.retrieveAndConvertSpecificRealEstateEntity(1))
            .thenReturn(realEstateDetailsSoldFlow)

        viewModel.getSpecificRealEstateConverted(1)

        val detailsUiStateSold =
            viewModel.detailsUiState.value as DetailsViewModel.DetailsUiState.Success

        assertEquals(false, detailsUiStateSold.realEstateData.isAvailable)
    }

    companion object {

        private val photo = RealEstatePhoto(
            "123",
            "123"
        )

        private val photoList = mutableListOf(photo)

        private val detailRe = RealEstate(
            type = "HOUSE",
            surface = 250,
            price = 1500000,
            description = "House",
            address = "France",
            nearbyPOI = null,
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

        private val detailReSold = detailRe.copy(
            isAvailable = false,
            saleDate = OffsetDateTime.now()
        )

        private val realEstateDetailsFlow = flowOf(detailRe)
        private val realEstateDetailsSoldFlow = flowOf(detailReSold)

    }
}