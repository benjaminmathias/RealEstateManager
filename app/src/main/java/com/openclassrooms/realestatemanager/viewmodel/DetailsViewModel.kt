package com.openclassrooms.realestatemanager.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val realEstateRepository: RealEstateRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState?>(null)
    val uiState: MutableStateFlow<DetailsUiState?> = _uiState

    fun getSpecificRealEstateConverted(id: Long) {
        viewModelScope.launch {
            realEstateRepository.retrieveAndConvertSpecificRealEstateEntity(id)
                .catch { e ->
                    _uiState.value = DetailsUiState.Error(e)
                }
                .collect {
                    _uiState.value = DetailsUiState.Success(it)
                }
        }
    }

    fun setRealEstateAsNoLongerAvailable(saleDate: String, isAvailable: Boolean, id: Long) = viewModelScope.launch {
        Log.d("DetailsViewModel", "Update called")
        realEstateRepository.setRealEstateAsNoLongerAvailable(saleDate, isAvailable, id)
    }

    sealed class DetailsUiState {
        data class Success(val realEstate: RealEstate) : DetailsUiState()
        data class Error(val exception: Throwable) : DetailsUiState()
    }
}