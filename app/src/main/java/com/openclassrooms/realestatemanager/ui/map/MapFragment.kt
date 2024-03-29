package com.openclassrooms.realestatemanager.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.AbstractListDetailFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.RealEstate
import com.openclassrooms.realestatemanager.data.model.UserLocation
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.TwoPaneOnBackPressedCallback
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : AbstractListDetailFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val markers = mutableListOf<Marker>()
    private lateinit var mMap: GoogleMap

    override fun onCreateListPaneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreateDetailPaneNavHostFragment(): NavHostFragment {
        return NavHostFragment.create(R.navigation.two_pane_navigation)
    }

    override fun onListPaneViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onListPaneViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            TwoPaneOnBackPressedCallback(slidingPaneLayout)
        )

        binding.mapFab.setOnClickListener {
            findNavController().navigate(R.id.fragmentList)
        }

        val mapView: MapView = binding.fragmentMapMapview
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        collectLocation()
    }

    private fun setupMap(userLocation: UserLocation) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.connectivityFlow.collect { value ->
                    when (value) {
                        true -> {
                            val latitude = userLocation.latitude
                            val longitude = userLocation.longitude

                            if (ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                mMap.isMyLocationEnabled = true
                            }
                            mMap.isMyLocationEnabled = true

                            val cameraPosition = CameraPosition.Builder()
                                .target(LatLng(latitude, longitude)).zoom(16f).build()
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                            collectState()
                            observeFilteredList()
                        }

                        false -> {
                            binding.fragmentMapMapview.visibility = View.GONE
                            binding.emptyView.root.visibility = View.VISIBLE
                            binding.emptyView.title.text = getString(R.string.map_internet_error_message_title)
                            binding.emptyView.subTitle.text =
                                getString(R.string.map_internet_error_message_text)

                        }
                    }
                }
            }
        }

    }

    private fun setupMarkers(realEstateList: List<RealEstate>) {
        mMap.clear()
        markers.clear()

        if (realEstateList.isEmpty()) return

        for (realEstate in realEstateList) {
            val lat: Double? = realEstate.lat
            val long: Double? = realEstate.lon
            if (lat != null && long != null) {
                val location = LatLng(lat, long)

                val mapMarker = if (realEstate.isAvailable) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    )
                } else {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                }
                mapMarker?.tag = realEstate.id

                if (mapMarker != null) {
                    markers.add(mapMarker)
                }
            }
        }

        mMap.setOnMarkerClickListener { marker ->
            Log.d("MapFragment", "marker position" + marker.position)
            val placeId = marker.tag as Long
            openDetails(placeId)
            return@setOnMarkerClickListener false
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is MainViewModel.MainUiState.Success -> {
                            setupMarkers(uiState.realEstateList)
                            binding.fragmentMapMapview.visibility = View.VISIBLE
                            binding.emptyView.root.visibility = View.GONE

                            if (uiState.realEstateList.isEmpty()) {
                                collectFilteredValue()
                            }

                            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                                resources.configuration.isLayoutSizeAtLeast(
                                    Configuration.SCREENLAYOUT_SIZE_LARGE
                                )
                            ) {
                                if (uiState.realEstateList.isNotEmpty()) {
                                    uiState.realEstateList[0].id?.let { openDetails(it) }
                                }
                            }
                        }

                        is MainViewModel.MainUiState.Error -> {
                            binding.emptyView.root.visibility = View.VISIBLE
                            binding.fragmentMapMapview.visibility = View.GONE
                            binding.emptyView.title.text = getString(R.string.list_error_message)
                        }
                    }
                }
            }
        }
    }

    private fun collectLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLocationFlow.collect {
                    setupMap(it)
                }
            }
        }
    }


    private fun collectFilteredValue() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFiltered.collect {
                    when (it) {
                        true -> {
                            if (markers.size == 0) {
                                binding.fragmentMapMapview.visibility = View.GONE
                                binding.emptyView.root.visibility = View.VISIBLE
                                binding.emptyView.title.text =
                                    getString(R.string.no_data_available_text)
                                binding.emptyView.subTitle.text =
                                    getString(R.string.no_result_query_text)
                            }
                        }

                        false -> {
                            if (markers.size == 0) {
                                binding.fragmentMapMapview.visibility = View.GONE
                                binding.emptyView.root.visibility = View.VISIBLE
                                binding.emptyView.title.text =
                                    getString(R.string.no_data_available_text)
                                binding.emptyView.subTitle.text =
                                    getString(R.string.no_property_text)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun observeFilteredList() {
        lifecycleScope.launch {
            viewModel.isFiltered.collect {
                if (it) {
                    if (markers.size == 0) {
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                            resources.configuration.isLayoutSizeAtLeast(
                                Configuration.SCREENLAYOUT_SIZE_LARGE
                            )
                        ) {
                            openDetails(null)
                        }
                    }
                    binding.realestateMapFilterClear?.visibility  = View.VISIBLE

                    binding.realestateMapFilterClear?.setOnClickListener {
                        viewModel.getNonFilteredList()
                    }
                } else {
                    binding.realestateMapFilterClear?.visibility = View.GONE
                }
            }
        }
    }

    private fun openDetails(itemId: Long?) {
        val bundle = Bundle().apply {
            if (itemId != null) {
                putLong("id", itemId)
            }
        }
        val detailNavController = detailPaneNavHostFragment.navController
        detailNavController.navigate(
            R.id.fragmentDetails,
            bundle,
            NavOptions.Builder()
                .setPopUpTo(detailNavController.graph.startDestinationId, true)
                .apply {
                    if (slidingPaneLayout.isOpen) {
                        setEnterAnim(R.animator.nav_default_enter_anim)
                        setExitAnim(R.animator.nav_default_exit_anim)
                    }
                }
                .build()
        )
        slidingPaneLayout.open()
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
    }
}