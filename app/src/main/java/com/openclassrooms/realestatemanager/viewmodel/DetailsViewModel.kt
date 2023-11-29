package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _realEstateDataDetails: MutableStateFlow<RealEstateData> =
        MutableStateFlow(RealEstateData())
    val realEstateDataDetailsFlow: StateFlow<RealEstateData> = _realEstateDataDetails

    private val _detailsUiState = MutableStateFlow<DetailsUiState?>(null)
    val detailsUiState = _detailsUiState.asStateFlow()

    private val realEstateId = savedStateHandle.get<Long>("id")

    init {
        viewModelScope.launch {
            if (realEstateId != null) {
                getSpecificRealEstateConverted(realEstateId)
            }
        }
    }

    fun getSpecificRealEstateConverted(id: Long) {
        viewModelScope.launch {
            realEstateRepository.retrieveAndConvertSpecificRealEstateEntity(id)
                .map { realEstate: RealEstate ->
                    RealEstateData(
                        type = realEstate.type,
                        surface = realEstate.surface.toString(),
                        price = realEstate.price.toString(),
                        description = realEstate.description,
                        address = realEstate.address,
                        isAvailable = realEstate.isAvailable,
                        photos = realEstate.photos as MutableList<RealEstatePhoto>,
                        nearbyPOI = realEstate.nearbyPOI,
                        entryDate = realEstate.entryDate,
                        saleDate = realEstate.saleDate,
                        assignedAgent = realEstate.assignedAgent,
                        room = realEstate.room.toString(),
                        bedroom = realEstate.bedroom.toString(),
                        bathroom = realEstate.bathroom.toString(),
                        lat = realEstate.lat,
                        lon = realEstate.lon,
                        id = realEstate.id
                    )
                }
                .catch { e ->
                    _detailsUiState.value = DetailsUiState.Error(e)
                }
                .collect {
                    _detailsUiState.value = DetailsUiState.Success(it)
                    _realEstateDataDetails.value = it
                }
        }
    }

    fun setRealEstateAsNoLongerAvailable(
        saleDate: OffsetDateTime,
        isAvailable: Boolean,
        id: Long,
    ) =
        viewModelScope.launch {
            realEstateRepository.setRealEstateAsNoLongerAvailable(saleDate, isAvailable, id)
        }

    sealed class DetailsUiState {

        data class Success(val realEstateData: RealEstateData) : DetailsUiState()
        data class Error(val exception: Throwable) : DetailsUiState()
    }
}