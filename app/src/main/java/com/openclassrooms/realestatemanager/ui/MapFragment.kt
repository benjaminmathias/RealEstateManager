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
import androidx.navigation.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Objects

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var mMap: GoogleMap
    private var mapMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)

        activity?.title = "Map"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView: MapView = binding.fragmentMapMapview
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true

        // Handle click on marker by retrieving marker's tag
        mMap.setOnMarkerClickListener { marker: Marker ->
            Log.d("MapFragment", "marker position" + marker.position)
            val placeId = marker.tag as Long
            val bundle = Bundle().apply {
                putLong("id", placeId)
            }
            requireView().findNavController().navigate(R.id.fragmentDetails, bundle)
            return@setOnMarkerClickListener false
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect {
                    when (it) {
                        is MainViewModel.MainUiState.Success -> {
                            setupMap(it.realEstateEntity)
                        }
                        else -> TODO()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupMap(realEstateList : List<RealEstate>) {
        for (realEstate in realEstateList){
            val location = LatLng(realEstate.lat, realEstate.lon)
            mapMarker = mMap.addMarker(MarkerOptions().position(location))

            Objects.requireNonNull(mapMarker)?.tag = realEstate.id

        }
        // TODO : inject userlocation interface to retrieve lat lng to animate camera

    }
}