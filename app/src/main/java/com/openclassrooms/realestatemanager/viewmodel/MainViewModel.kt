package com.openclassrooms.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.data.RealEstate
import com.openclassrooms.realestatemanager.model.repo.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainUiState> =
        MutableStateFlow(MainUiState.Success(emptyList()))
    val uiState: StateFlow<MainUiState> = _uiState

    /*val uiStateLiveData: LiveData<MainUiState> =
       _uiState.asLiveData(Dispatchers.Main)


    private val _isFiltered: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val filtered: StateFlow<Boolean> = _isFiltered*/

    init {
        getNonFilteredList()


        /*filterList(
            type = null,
            priceMin = null,
            priceMax = "250",
            surfaceMin = null,
            surfaceMax = null,
            room = null,
            bedroom = null,
            bathroom = null,
            location = null,
            nearbyPOI = null
        )*/
    }

    private fun getNonFilteredList() {
        viewModelScope.launch {
            realEstateRepository.retrieveAndConvertRealEstateEntityList()
                .catch { e ->
                    _uiState.value = MainUiState.Error(e)
                    Log.d("MVM", "Error : " + e.message)
                }
                .collect {
                     _uiState.value = MainUiState.Success(it)
                    Log.d("MVM", "Base list success !")
                    // _isFiltered.value = false
                }
        }
    }

    fun filterList(
        type: String?,
        priceMin: String?,
        priceMax: String?,
        surfaceMin: String?,
        surfaceMax: String?,
        room: String?,
        bedroom: String?,
        bathroom: String?,
        location: String?,
        nearbyPOI: List<String>?
    ) {
        viewModelScope.launch {
            realEstateRepository.retrieveAndConvertFilteredRealEstateList(
                type = type,
                priceMin = priceMin,
                priceMax = priceMax,
                surfaceMin = surfaceMin,
                surfaceMax = surfaceMax,
                room = room,
                bedroom = bedroom,
                bathroom = bathroom,
                location = location,
                nearbyPOI = nearbyPOI
            )
                .catch { e ->
                     _uiState.value = MainUiState.Error(e)
                    Log.d("MVM", "Error : " + e.message)
                }
                .collect {
                    Log.d("MVM", "Success")
                    _uiState.value = MainUiState.Success(it)
                    // _uiState.update { uiState -> MainUiState.Success(it) }
                    // _isFiltered.value = true
                    Log.d("MVM", "Item(s) : " + it.size)
                }


            /* try {
                val it = realEstateRepository.retrieveAndConvertFilteredRealEstateList(
                type = type,
                priceMin = priceMin,
                priceMax = priceMax,
                surfaceMin = surfaceMin,
                surfaceMax = surfaceMax,
                room = room,
                bedroom = bedroom,
                bathroom = bathroom,
                location = location,
                nearbyPOI = nearbyPOI
            )
                Log.d("MVM", "Success")
                _uiState.value = MainUiState.Success(it)
                Log.d("MVM", "Amount of item : " + it.size)
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e)
                Log.d("MVM", "Error : " + e.message)
            }
        }*/

        }
    }

    sealed class MainUiState {

        data class Success(val realEstateEntity: List<RealEstate> = mutableListOf()) : MainUiState()

        data class Filter(val realEstateFilteredList: List<RealEstate> = mutableListOf()) : MainUiState()

        data class Error(val exception: Throwable) : MainUiState()
    }
}