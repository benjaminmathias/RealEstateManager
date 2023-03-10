package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val realEstateRepository: RealEstateRepository) : ViewModel() {

    val allRealEstate : LiveData<List<RealEstate>> = realEstateRepository.allRealEstates

    fun addRealEstate(realEstate : RealEstate) = viewModelScope.launch {
        realEstateRepository.insert(realEstate)
    }

}