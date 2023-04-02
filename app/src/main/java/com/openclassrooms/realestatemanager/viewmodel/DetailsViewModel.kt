package com.openclassrooms.realestatemanager.viewmodel

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

    /* lateinit var mSpeficicRealEstate: LiveData<RealEstate>

     fun getSpecificRealEstateConverted(id: Long) {
         mSpeficicRealEstate = realEstateRepository.retrieveAndConvertSpecificRealEstateEntity(id)
     }*/

    private val _uiState = MutableStateFlow<DetailsUiState?>(null)
    val uiState: MutableStateFlow<DetailsUiState?> = _uiState

    fun getSpecificRealEstateConverted(id: Long) {
        viewModelScope.launch {
            realEstateRepository.retrieveAndConvertSpecificRealEstateEntityTest(id)
                .catch { e ->
                    _uiState.value = DetailsUiState.Error(e)
                }
                .collect {
                    _uiState.value = DetailsUiState.Success(it)
                }
        }
    }

    sealed class DetailsUiState {
        data class Success(val realEstate: RealEstate) : DetailsUiState()
        data class Error(val exception: Throwable) : DetailsUiState()
    }
}