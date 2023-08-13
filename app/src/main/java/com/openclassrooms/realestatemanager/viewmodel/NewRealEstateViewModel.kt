package com.openclassrooms.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstatePhoto
import com.openclassrooms.realestatemanager.data.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.LocationService
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
    private val locationService: LocationService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val realEstateId = savedStateHandle.get<Long>("id")

    // private val _retrievedLocation: MutableSharedFlow<UserLocation> = MutableSharedFlow(replay = 1)
    //val retrievedLocationFlow: SharedFlow<UserLocation> = _retrievedLocation

    private val _realEstateData: MutableStateFlow<RealEstateData> =
        MutableStateFlow(RealEstateData())
    val realEstateDataFlow: StateFlow<RealEstateData> = _realEstateData

    private val _uiState: MutableStateFlow<RealEstateUiState> =
        MutableStateFlow(RealEstateUiState.Default(true))
    val uiState: StateFlow<RealEstateUiState> = _uiState

    val checkedTypeChipObservable: MutableStateFlow<List<Int>> = MutableStateFlow(mutableListOf())
    val checkedPoiChipObservable: MutableStateFlow<List<Int>> = MutableStateFlow(mutableListOf())
    val checkedAgentChipObservable: MutableStateFlow<List<Int>> = MutableStateFlow(mutableListOf())

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

        viewModelScope.launch {
            locationService.userLocation.collect {
                Log.d("Initial flow in VM", it.address)
                // _retrievedLocation.emit(it)
                _realEstateData.value = _realEstateData.value.copy(
                    address = it.address,
                    lat = it.latitude,
                    lon = it.longitude
                )
                //   Log.d("Flow value 0 in VM", _retrievedLocation.replayCache[0].address)
            }
        }
    }

    // GET ADDRESS

    // TYPE //
    // Retrieve currently selected real estate type
    private fun getSelectedRealEstateType(): MutableList<String> {

        val realEstateTypeList = mutableListOf<String>()

        if (checkedTypeChipObservable.value.isNotEmpty()) {
            for (id in checkedTypeChipObservable.value) {
                if (id == R.id.first_type_chip)
                    realEstateTypeList.add(RealEstateType.HOUSE.name)
                if (id == R.id.second_type_chip)
                    realEstateTypeList.add(RealEstateType.APARTMENT.name)
                if (id == R.id.third_type_chip)
                    realEstateTypeList.add(RealEstateType.CONDO.name)
                if (id == R.id.fourth_type_chip)
                    realEstateTypeList.add(RealEstateType.LOFT.name)
                if (id == R.id.fifth_type_chip)
                    realEstateTypeList.add(RealEstateType.MANSION.name)
                if (id == R.id.sixth_type_chip)
                    realEstateTypeList.add(RealEstateType.VILLA.name)
            }
        }
        return realEstateTypeList
    }

    // Retrieve stored real estate type
    private fun getStoredRealEstateType() {
        val chipId = when (realEstateDataFlow.value.type) {
            RealEstateType.HOUSE.name -> R.id.first_type_chip
            RealEstateType.APARTMENT.name -> R.id.second_type_chip
            RealEstateType.CONDO.name -> R.id.third_type_chip
            RealEstateType.LOFT.name -> R.id.fourth_type_chip
            RealEstateType.MANSION.name -> R.id.fifth_type_chip
            RealEstateType.VILLA.name -> R.id.sixth_type_chip
            else -> 0
        }

        val chipIdList = mutableListOf<Int>()
        chipIdList.add(chipId)

        checkedTypeChipObservable.value = chipIdList
    }

    // POI //
    // Retrieve currently selected poi chips
    private fun getSelectedRealEstatePoi(): MutableList<String> {

        val realEstatePoiList = mutableListOf<String>()

        if (checkedPoiChipObservable.value.isNotEmpty()) {
            for (id in checkedPoiChipObservable.value) {
                if (id == R.id.restaurant_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.RESTAURANT.name)
                if (id == R.id.shopping_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.SHOPPING.name)
                if (id == R.id.school_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.SCHOOL.name)
                if (id == R.id.healthcare_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.HEALTHCARE.name)
                if (id == R.id.park_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.PARK.name)
                if (id == R.id.sports_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.SPORTS.name)
                if (id == R.id.market_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.MARKET.name)
                if (id == R.id.entertainment_poi_chip)
                    realEstatePoiList.add(RealEstatePoi.ENTERTAINMENT.name)
            }
        }
        return realEstatePoiList
    }

    // Retrieve stored real estate poi list
    private fun getStoredRealEstatePoi() {
        val chipIds = mutableListOf<Int>()
        for (poi in realEstateDataFlow.value.nearbyPOI) {
            if (poi == RealEstatePoi.RESTAURANT.name)
                chipIds.add(R.id.restaurant_poi_chip)
            if (poi == RealEstatePoi.SHOPPING.name)
                chipIds.add(R.id.shopping_poi_chip)
            if (poi == RealEstatePoi.SCHOOL.name)
                chipIds.add(R.id.school_poi_chip)
            if (poi == RealEstatePoi.HEALTHCARE.name)
                chipIds.add(R.id.healthcare_poi_chip)
            if (poi == RealEstatePoi.PARK.name)
                chipIds.add(R.id.park_poi_chip)
            if (poi == RealEstatePoi.SPORTS.name)
                chipIds.add(R.id.sports_poi_chip)
            if (poi == RealEstatePoi.MARKET.name)
                chipIds.add(R.id.market_poi_chip)
            if (poi == RealEstatePoi.ENTERTAINMENT.name)
                chipIds.add(R.id.entertainment_poi_chip)
        }
        checkedPoiChipObservable.value = chipIds
    }


    // AGENT //
    // Retrieve currently selected real estate agent
    private fun getSelectedRealEstateAgent(): MutableList<String> {

        val realEstateAgentList = mutableListOf<String>()

        if (checkedAgentChipObservable.value.isNotEmpty()) {
            for (id in checkedAgentChipObservable.value) {
                if (id == R.id.first_agent_chip)
                    realEstateAgentList.add(RealEstateAgent.JACK.name)
                if (id == R.id.second_agent_chip)
                    realEstateAgentList.add(RealEstateAgent.WALTER.name)
                if (id == R.id.third_agent_chip)
                    realEstateAgentList.add(RealEstateAgent.TED.name)
                if (id == R.id.fourth_agent_chip)
                    realEstateAgentList.add(RealEstateAgent.ELLIE.name)
                if (id == R.id.fifth_agent_chip)
                    realEstateAgentList.add(RealEstateAgent.ROBIN.name)
                if (id == R.id.sixth_agent_chip)
                    realEstateAgentList.add(RealEstateAgent.KATE.name)
            }
        }
        return realEstateAgentList
    }

    // Retrieve stored real estate agent
    private fun getStoredRealEstateAgent() {
        val chipId = when (realEstateDataFlow.value.assignedAgent) {
            RealEstateAgent.JACK.name -> R.id.first_agent_chip
            RealEstateAgent.WALTER.name -> R.id.second_agent_chip
            RealEstateAgent.TED.name -> R.id.third_agent_chip
            RealEstateAgent.ELLIE.name -> R.id.fourth_agent_chip
            RealEstateAgent.ROBIN.name -> R.id.fifth_agent_chip
            RealEstateAgent.KATE.name -> R.id.sixth_agent_chip
            else -> 0
        }

        val chipIdList = mutableListOf<Int>()
        chipIdList.add(chipId)

        checkedAgentChipObservable.value = chipIdList
    }

    // Submit
    fun onSaveButtonClick() {
        viewModelScope.launch {
            if (checkIfFieldsAreValid()) {
                val realEstate = getRealEstate(_realEstateData.value)
                // retrieveAddress(_userLocation.value.latitude, _userLocation.value.longitude)
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

    // Retrieve
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
                    photos = realEstate.photos as MutableList<RealEstatePhoto>,
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
                getStoredRealEstateType()
                getStoredRealEstatePoi()
                getStoredRealEstateAgent()
                Log.d("VM", "Retrieved RealEstateData")
            }
    }

    private fun checkIfFieldsAreValid(): Boolean {
        if (getSelectedRealEstateType().toString().isEmpty() ||
            _realEstateData.value.price.isEmpty() ||
            _realEstateData.value.surface.isEmpty() ||
            _realEstateData.value.description.isEmpty() ||
            _realEstateData.value.room.isEmpty() ||
            _realEstateData.value.bedroom.isEmpty() ||
            _realEstateData.value.bathroom.isEmpty() ||
            _realEstateData.value.photos.isEmpty() ||
            _realEstateData.value.entryDate == "../../...." ||
            getSelectedRealEstateAgent().toString().isEmpty() ||
            getSelectedRealEstatePoi().isEmpty()
        ) {
            return false
        }
        return true
    }

    // Get the current value of all fields
    private fun getRealEstate(realEstateData: RealEstateData): RealEstate {
        return RealEstate(
            type = getSelectedRealEstateType()[0],
            surface = realEstateData.surface.toInt(),
            price = realEstateData.price.toInt(),
            description = realEstateData.description,
            address = realEstateData.address,
            isAvailable = realEstateData.isAvailable,
            nearbyPOI = getSelectedRealEstatePoi(),
            entryDate = realEstateData.entryDate,
            saleDate = null,
            assignedAgent = getSelectedRealEstateAgent()[0],
            room = realEstateData.room.toInt(),
            bedroom = realEstateData.bedroom.toInt(),
            bathroom = realEstateData.bathroom.toInt(),
            id = realEstateData.id,
            photos = realEstateData.photos,
            lat = realEstateData.lat,
            lon = realEstateData.lon
        )
        // TODO : var name check
    }
}

data class RealEstateData(
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
    var photos: MutableList<RealEstatePhoto> = mutableListOf(),
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    var id: Long? = null
)

sealed class RealEstateUiState {
    data class RealEstateDataUi(val realEstateData: RealEstateData) : RealEstateUiState()
    data class Success(val success: Boolean) : RealEstateUiState()
    data class Error(val error: Boolean, val id: UUID = UUID.randomUUID()) : RealEstateUiState()
    data class Default(val default: Boolean) : RealEstateUiState()
}

enum class RealEstateType {
    HOUSE, APARTMENT, CONDO, LOFT, MANSION, VILLA
}

enum class RealEstatePoi {
    RESTAURANT, SHOPPING, SCHOOL, HEALTHCARE, PARK, SPORTS, MARKET, ENTERTAINMENT
}

enum class RealEstateAgent {
    JACK, WALTER, TED, ELLIE, ROBIN, KATE
}