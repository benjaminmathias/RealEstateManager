package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(private val realEstateRepository: RealEstateRepository) :
    ViewModel() {

    fun addRealEstateDTO(realEstate: RealEstate) = viewModelScope.launch {
        realEstateRepository.insertRealEstateAndPhoto(realEstate)
    }
}