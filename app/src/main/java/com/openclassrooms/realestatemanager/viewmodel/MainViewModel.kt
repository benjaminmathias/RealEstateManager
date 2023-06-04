package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstateRepository
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

    init {
        viewModelScope.launch {
            realEstateRepository.retrieveAndConvertRealEstateEntityList()
                .catch { e ->
                    _uiState.value = MainUiState.Error(e)
                }
                .collect {
                    _uiState.value = MainUiState.Success(it)
                }
        }
    }

    sealed class MainUiState {
        data class Success(val realEstateEntity: List<RealEstate>) : MainUiState()
        data class Error(val exception: Throwable) : MainUiState()
    }
}