package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.PhotoItem
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewRealEstateViewModel @Inject constructor(
    private val realEstateRepository: RealEstateRepository,
) : ViewModel() {

    var realEstateData = RealEstateData()

    fun addRealEstateDTO(realEstate: RealEstate) = viewModelScope.launch {
        realEstateRepository.insertRealEstateAndPhoto(realEstate)
    }

    fun onSaveButtonClick() {
        // Recuperer tout les champs
        // Validation des champs
        // Si erreur emettre l'erreur et le fragment devra observer cette erreur : un flow/stateflow
        // Si valide : Construire le realEstate et appeler le repo pour save

        val realEstate = RealEstate(
            type = realEstateData.type,
            surface = realEstateData.surface.toInt(),
            price = realEstateData.price.toInt(),
            description = realEstateData.description,
            address = realEstateData.address,
            isAvailable = realEstateData.isAvailable,
            nearbyPOI = realEstateData.nearbyPOI,
            entryDate = realEstateData.entryDate,
            saleDate = null,
            assignedAgent = realEstateData.assignedAgent,
            room = realEstateData.room.toInt(),
            bedroom = realEstateData.bedroom.toInt(),
            bathroom = realEstateData.bathroom.toInt(),
            id = realEstateData.id,
            photos = realEstateData.photos
        )

        addRealEstateDTO(realEstate = realEstate)
    }

    fun getTypeChip(type: String) {
        realEstateData.type = type
    }

    fun getPoiChip(poi: ArrayList<String>) {
        realEstateData.nearbyPOI = poi
    }

    fun getPhotos(photos: MutableList<PhotoItem>) {
        realEstateData.photos = photos
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
    var nearbyPOI: ArrayList<String> = ArrayList(),
    var photos: MutableList<PhotoItem> = mutableListOf(),
    var id: Long? = null
)