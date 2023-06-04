package com.openclassrooms.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.PhotoItem
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewRealEstateViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val realEstateId = savedStateHandle.get<Long>("id")

    private val _realEstateData: MutableStateFlow<RealEstateData> =
        MutableStateFlow(RealEstateData())
    val realEstateDataFlow: StateFlow<RealEstateData> = _realEstateData

    private val _uiState: MutableStateFlow<RealEstateUiState> =
        MutableStateFlow(RealEstateUiState.Default(true))
    val uiState: StateFlow<RealEstateUiState> = _uiState

  //  val checkedBtnObs = ObservableInt(RealEstateType.valueOf(realEstateDataFlow.value.type).ordinal)

    init {
        viewModelScope.launch {
            if (realEstateId == null) {
                _uiState.value = RealEstateUiState.Default(true)
                _uiState.value = RealEstateUiState.RealEstateDataUi(_realEstateData.value)
                Log.d("VM", "Default RealEstateData")
            } else {
                _uiState.value = RealEstateUiState.Default(false)
                retrieveStoredRealEstate(realEstateId)
                Log.d("VM", "Retrieved RealEstateData")
            }
        }
    }

    fun onRealEstateTypeSelected(type: RealEstateType) {
        Log.d("CHIP", "Selected type : $type")
        _realEstateData.value.type = type.name
        Log.d("CHIP", "New real estate type value : ${_realEstateData.value.type}")
    }

    fun getRealEstateTypeSelected(): Int {
        var position = 0
        if (realEstateId != null) {
            position = RealEstateType.valueOf(realEstateDataFlow.value.type).ordinal
        }
        return position
    }

    /*
    fun onNearbyPoiSelected(poi: List<RealEstateNearbyPoi>){
        realEstateData.nearbyPOI = listOf(poi.toString())
        // in xml
        //android:checked="@{viewModel.realEstateData.nearbyPOI == poi.add(RealEstateNearbyPoi.RESTAURANT.name())}"
        //android:onClick="@{() -> viewModel.onNearbyPoiSelected(poi.add(RealEstateNearbyPoi.RESTAURANT.name()))}"
    }*/

    fun onSaveButtonClick() {
        viewModelScope.launch {
            if (checkIfFieldsAreValid()) {
                val realEstate = getRealEstate(_realEstateData.value)
                // If id is null, create a new RealEstate
                if (realEstateId == null) {
                    realEstateRepository.insertRealEstateAndPhoto(realEstate)
                } else {
                    // If id isn't null, edit existing RealEstate
                    _realEstateData.value.id = realEstateId
                    realEstateRepository.editRealEstate(realEstate)
                }
                _uiState.value = RealEstateUiState.Success(true)
            } else {
                _uiState.value = RealEstateUiState.Error(true)
            }
        }
    }

    private suspend fun retrieveStoredRealEstate(id: Long) {
        realEstateRepository.retrieveAndConvertSpecificRealEstateEntity(id)
            .map { realEstate: RealEstate ->
                RealEstateData(
                    type = realEstate.type,
                    surface = realEstate.surface.toString(),
                    price = realEstate.price.toString(),
                    description = realEstate.description,
                    address = realEstate.address,
                    isAvailable = realEstate.isAvailable,
                    photos = realEstate.photos as MutableList<PhotoItem>,
                    nearbyPOI = realEstate.nearbyPOI,
                    entryDate = realEstate.entryDate,
                    assignedAgent = realEstate.assignedAgent,
                    room = realEstate.room.toString(),
                    bedroom = realEstate.bedroom.toString(),
                    bathroom = realEstate.bathroom.toString(),
                    id = realEstate.id
                )
            }
            .catch { e ->
                _uiState.value = RealEstateUiState.Error(true)
            }
            .collect {
                _realEstateData.value = it
            }
    }


    private fun checkIfFieldsAreValid(): Boolean {
        if (_realEstateData.value.type.isEmpty() || _realEstateData.value.price.isEmpty() ||
            _realEstateData.value.surface.isEmpty() || _realEstateData.value.description.isEmpty() ||
            _realEstateData.value.room.isEmpty() || _realEstateData.value.bedroom.isEmpty() ||
            _realEstateData.value.bathroom.isEmpty() || _realEstateData.value.address.isEmpty() ||
            _realEstateData.value.entryDate == "../../...." || _realEstateData.value.assignedAgent.isEmpty() ||
            _realEstateData.value.nearbyPOI.isEmpty()
        ) {
            return false
        }
        return true
    }

    private fun getRealEstate(realEstateData: RealEstateData): RealEstate {
        return RealEstate(
            type = realEstateData.type,
            surface = realEstateData.surface.toInt(),
            price = realEstateData.price.toInt(),
            description = realEstateData.description,
            address = realEstateData.address,
            isAvailable = realEstateData.isAvailable,
            nearbyPOI = realEstateData.nearbyPOI.toList(),
            entryDate = realEstateData.entryDate,
            saleDate = null,
            assignedAgent = realEstateData.assignedAgent,
            room = realEstateData.room.toInt(),
            bedroom = realEstateData.bedroom.toInt(),
            bathroom = realEstateData.bathroom.toInt(),
            id = realEstateData.id,
            photos = realEstateData.photos
        )
    }
}

class RealEstateData(
    var type: String = "",
    var price: String = "",
    var surface: String = "",
    var description: String = "",
    var room: String = "",
    var bedroom: String = "",
    var bathroom: String = "",
    var address: String = "",
    var entryDate: String = "../../....",
    var assignedAgent: String = "",
    var isAvailable: Boolean = true,
    var nearbyPOI: List<String> = emptyList(),
    var photos: MutableList<PhotoItem> = mutableListOf(),
    var id: Long? = null
)

sealed class RealEstateUiState {
    data class RealEstateDataUi(val realEstateData: RealEstateData) : RealEstateUiState()
    data class Success(val success: Boolean) : RealEstateUiState()
    data class Error(val error: Boolean, val id: UUID = UUID.randomUUID()) : RealEstateUiState()
    data class Default(val default: Boolean) : RealEstateUiState()
}

enum class RealEstateType {
    HOUSE, APARTMENT, CONDO, LOFT, MANSION, VILLA;
}

enum class RealEstateNearbyPoi {
    RESTAURANT, SHOPPING, SCHOOL, HEALTHCARE, PARK, SOMETHING, ELSE, GOES, HERE
}