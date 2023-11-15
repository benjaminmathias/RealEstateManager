package com.openclassrooms.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.data.Filters
import com.openclassrooms.realestatemanager.model.data.RealEstate
import com.openclassrooms.realestatemanager.model.data.UserLocation
import com.openclassrooms.realestatemanager.model.repo.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.LocationService
import com.openclassrooms.realestatemanager.utils.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
    private val locationService: LocationService,
    private val connectivityObserver: NetworkObserver
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainUiState> =
        MutableStateFlow(MainUiState.Success(emptyList()))
    val uiState = _uiState.asStateFlow()

    private val _isFiltered: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isFiltered = _isFiltered.asStateFlow()

    private val _closeDialog: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val closeDialog = _closeDialog.asStateFlow()

    private val _noQuery: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val noQuery = _noQuery.asStateFlow()

    private val _realEstateFilters: MutableStateFlow<Filters> =
        MutableStateFlow(Filters())
    val realEstateFilters = _realEstateFilters.asStateFlow()

    val checkedTypeChipObservable: MutableStateFlow<List<Int>> = MutableStateFlow(mutableListOf())
    val checkedPoiChipObservable: MutableStateFlow<List<Int>> = MutableStateFlow(mutableListOf())
    var checkedAvailableRadioButtonObservable: MutableStateFlow<Int> = MutableStateFlow(0)

    val userLocationLiveData: MutableLiveData<UserLocation> = MutableLiveData()
    val connectivityLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val userLocationFlow : Flow<UserLocation> = locationService.userLocation

    init {
        checkedAvailableRadioButtonObservable.value = R.id.radio_ignore
        getNonFilteredList()
        getCurrentLocation()
        getCurrentConnectivity()
    }

    fun getNonFilteredList() {
        viewModelScope.launch {
            realEstateRepository.retrieveAndConvertRealEstateEntityList()
                .catch { e ->
                    _uiState.value = MainUiState.Error(e)
                    Log.d("MVM", "Error : " + e.message)
                }
                .collect {
                    _isFiltered.emit(false)
                    _uiState.value = MainUiState.Success(it)
                    Log.d("MVM", "Base list success !")
                }
        }
    }

    fun filterList() {
        viewModelScope.launch {
            if (checkIfFieldsAreValid()) {
                val filters = getFilters(_realEstateFilters.value)
                val filteredList =
                    realEstateRepository.retrieveAndConvertFilteredRealEstateList(filters)
                _uiState.value = MainUiState.Success(filteredList)
                _isFiltered.emit(true)
                _closeDialog.emit(true)
                _closeDialog.emit(false)
                Log.d("MVM", "Success")
                Log.d("isFiltered value", _isFiltered.value.toString())
                Log.d("MVM", "Amount of item : " + filteredList.size)
            } else {
                _noQuery.emit(true)
                Log.d("noQuery value", _noQuery.value.toString())
            }
        }
    }

    // Get user location
    private fun getCurrentLocation(): LiveData<UserLocation> {
        viewModelScope.launch {
            locationService.userLocation.collect {
                Log.d("Initial flow in VM", it.address)
                userLocationLiveData.value = it
            }
        }
        return userLocationLiveData
    }

    private fun getCurrentConnectivity(): LiveData<Boolean> {
        viewModelScope.launch {
            connectivityObserver.isConnected.collect {
                connectivityLiveData.value = it
            }
        }
        return connectivityLiveData
    }

    private fun getSelectedRealEstateType(): MutableList<String>? {

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
        } else {
            return null
        }
        return realEstateTypeList
    }

    private fun getSelectedAvailability(): Boolean? {
        var isAvailable: Boolean? = null
        val checkedId: Int

        if (checkedAvailableRadioButtonObservable.value != 0) {
            checkedId = checkedAvailableRadioButtonObservable.value
            if (checkedId == R.id.radio_no) {
                isAvailable = false
            } else if (checkedId == R.id.radio_yes) {
                isAvailable = true
            }
        }
        return isAvailable
    }

    private fun getSelectedRealEstatePoi(): MutableList<String>? {

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
        } else {
            return null
        }
        return realEstatePoiList
    }

    fun setListedDateTo(listedTo: OffsetDateTime?) {
        _realEstateFilters.value = _realEstateFilters.value.copy(
            toListedDate = listedTo
        )
        Log.d("MVM", _realEstateFilters.value.toListedDate.toString())
    }

    fun setListedDateFrom(listedFrom: OffsetDateTime?) {
        _realEstateFilters.value = _realEstateFilters.value.copy(
            fromListedDate = listedFrom
        )
        Log.d("MVM", _realEstateFilters.value.fromListedDate.toString())
    }

    fun setSoldDateTo(soldTo: OffsetDateTime?) {
        _realEstateFilters.value = _realEstateFilters.value.copy(
            toSoldDate = soldTo
        )
    }

    fun setSoldDateFrom(soldFrom: OffsetDateTime?) {
        _realEstateFilters.value = _realEstateFilters.value.copy(
            fromSoldDate = soldFrom
        )
    }

    fun setNoQueryToFalse() {
        _noQuery.value = false
    }

    fun clearData() {
        _realEstateFilters.value = Filters()
        checkedPoiChipObservable.value = emptyList()
        checkedTypeChipObservable.value = emptyList()
    }

    private fun checkIfFieldsAreValid(
    ): Boolean {
        return getSelectedRealEstateType()?.get(0) != null ||
                getSelectedRealEstatePoi() != null ||
                getSelectedRealEstatePoi()?.isNotEmpty() == true ||
                getSelectedAvailability() != null ||
                _realEstateFilters.value.priceMin != null ||
                _realEstateFilters.value.priceMax != null ||
                _realEstateFilters.value.surfaceMin != null ||
                _realEstateFilters.value.surfaceMax != null ||
                _realEstateFilters.value.location != null ||
                _realEstateFilters.value.fromListedDate != null ||
                _realEstateFilters.value.toListedDate != null ||
                _realEstateFilters.value.fromSoldDate != null ||
                _realEstateFilters.value.toSoldDate != null
    }

    private fun getFilters(filters: Filters): Filters {
        return Filters(
            type = getSelectedRealEstateType()?.get(0),
            priceMin = currencyToString(filters.priceMin),
            priceMax = currencyToString(filters.priceMax),
            surfaceMin = filters.surfaceMin,
            surfaceMax = filters.surfaceMax,
            location = filters.location,
            nearbyPOI = getSelectedRealEstatePoi(),
            fromListedDate = filters.fromListedDate,
            toListedDate = filters.toListedDate,
            fromSoldDate = filters.fromSoldDate,
            toSoldDate = filters.toSoldDate,
            isAvailable = getSelectedAvailability()
        )
    }

    private fun currencyToString(price: String?): String? {
        if (price != null) {
            return price
                .replace("$", "")
                .replace(".", "")
                .replace(",", "")
        }
        return null
    }

    sealed class MainUiState {

        data class Success(val realEstateList: List<RealEstate>) : MainUiState()

        data class Error(val exception: Throwable) : MainUiState()
    }
}