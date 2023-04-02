package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsBinding
import com.openclassrooms.realestatemanager.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentDetails : Fragment() {

    private lateinit var binding: FragmentDetailsBinding

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)

        val id = this.arguments?.getLong("id")
        if (id != null) {
            getRealEstate(id)
        }
        Log.d("Details", "Details id = $id")

        updateUI()
        //observeRealEstate()

        return binding.root
    }

    private fun getRealEstate(id: Long) {
        viewModel.getSpecificRealEstateConverted(id)
    }

    private fun updateUI(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when(uiState){
                        is DetailsViewModel.DetailsUiState.Success ->
                            setupView(uiState.realEstate)
                        else -> {setupEmptyView()}
                    }
                }
            }
        }
    }

/*
    private fun observeRealEstate() {
        viewModel.mSpeficicRealEstate.observe(viewLifecycleOwner, fun(realEstate: RealEstate?) {
            if (realEstate != null) {
                setupView(realEstate)
            }
        })
    }*/

    // TODO : display error
    @SuppressLint("SetTextI18n")
    private fun setupEmptyView(){
        binding.typeTextView.text = "Error"
    }

    // TODO : remove Suppress once strings are added in xml
    @SuppressLint("SetTextI18n")
    private fun setupView(realEstate: RealEstate) {
        binding.typeTextView.text = realEstate.type
        binding.surfaceTextView.text = realEstate.surface.toString() + " m²"
        binding.priceTextView.text = realEstate.price.toString() + "€"
        binding.descriptionTextView.text = realEstate.description
        binding.locationTextView.text = realEstate.address
        binding.entrydateTextView.text = "Date listed : " + realEstate.entryDate
        binding.saledateTextView.text = realEstate.saleDate
        binding.assignedAgentTextView.text = realEstate.assignedAgent
        binding.roomTextView.text = "Number of rooms : " + realEstate.room.toString()
        binding.bedroomTextView.text = "Number of bedrooms : " + realEstate.bedroom.toString()
        binding.bathroomTextView.text = "Number of bathrooms : " + realEstate.bathroom.toString()

        if (realEstate.isAvailable) {
            binding.availabilityTextView.text = "This estate has been sold"
        } else {
            binding.availabilityTextView.text = "This estate is available !"
        }
    }
}