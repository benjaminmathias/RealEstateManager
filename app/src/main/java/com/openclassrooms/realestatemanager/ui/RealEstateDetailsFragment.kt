package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.data.RealEstatePhoto
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsBinding
import com.openclassrooms.realestatemanager.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RealEstateDetailsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentDetailsBinding

    private val viewModel: DetailsViewModel by viewModels()

    lateinit var viewPager: ViewPager
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var tabLayout: TabLayout
    lateinit var imageList: List<RealEstatePhoto>
    private lateinit var soldButton: Button

    private lateinit var mMap: GoogleMap

    var saleDate: String = ""
    var cal: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)

        val id = this.arguments?.getLong("id")
        if (id != null) {
            getRealEstate(id)
        }
        Log.d("Details", "Details id = $id")

        activity?.title = "Real Estate details"
        soldButton = binding.soldButton

        updateUI()

        binding.editButton.setOnClickListener {
            val bundle = Bundle().apply {
                if (id != null) {
                    putLong("id", id)
                }
            }
            requireView().findNavController().navigate(R.id.fragmentInput, bundle)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView: MapView = binding.detailsMap
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun setupMap(lat: Double, long: Double) {
        val realEstateLocation = LatLng(lat, long)
        val marker = MarkerOptions().position(realEstateLocation)
       // mMap.setMyLocationEnabled(true)
        mMap.addMarker(marker)
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(lat, long)).zoom(16f).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun updateUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is DetailsViewModel.DetailsUiState.Success -> {
                            setupView(uiState.realEstate)
                            setupMap(uiState.realEstate.lat, uiState.realEstate.lon)
                        }
                        else -> {
                            setupEmptyView()
                        }
                    }
                }
            }
        }
    }

    private fun getRealEstate(id: Long) {
        viewModel.getSpecificRealEstateConverted(id)
    }

    // TODO : display error
    @SuppressLint("SetTextI18n")
    private fun setupEmptyView() {
        binding.typeTextView.text = "Error"
    }

    // TODO : update without deprecated function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun setupSoldDialog(id: Long, minDay: String, minMonth: String, minYear: String) {
        soldButton.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Mark this property as sold")
            dialog.setMessage("Click on the `Selected sale date` button to select the sale date of this property")

            dialog.setNegativeButton("Cancel") { _, which ->
                Toast.makeText(
                    requireContext(),
                    "You didn't mark this property as sold",
                    Toast.LENGTH_LONG
                ).show()
            }

            dialog.setNeutralButton("Select sale date") { _, which ->
                val dateSetListener =
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        cal.set(minYear.toInt(), minMonth.toInt(), minDay.toInt())
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val myFormat = "dd/MM/yyyy"
                        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
                        saleDate = sdf.format(cal.time)
                        dialog.setMessage("This property will be mark as sold with the corresponding sale date : $saleDate")

                        dialog.setPositiveButton("Confirm") { _, which ->
                            updateRealEstate(id)
                        }
                        dialog.show()
                    }

                val picker =
                    DatePickerDialog(
                        requireContext(),
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    )
                cal.set(minYear.toInt(), minMonth.toInt() - 1, minDay.toInt())
                picker.datePicker.minDate = cal.timeInMillis
                picker.show()
            }
            dialog.show()
        }
    }

    private fun updateRealEstate(id: Long) {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
        val saleDate = sdf.format(cal.time)
        viewModel.setRealEstateAsNoLongerAvailable(saleDate, false, id)
        binding.soldButton.visibility = GONE
    }

    // TODO : remove Suppress once strings are added in xml
    @SuppressLint("SetTextI18n")
    private fun setupView(realEstate: RealEstate) {
        binding.typeTextView.text = realEstate.type
        binding.surfaceTextView.text = realEstate.surface.toString() + " m²"
        binding.priceTextView.text = "$" + realEstate.price.toString()
        binding.descriptionTextView.text = realEstate.description
        binding.locationTextView.text = realEstate.address
        binding.entrydateTextView.text = "Date listed : " + realEstate.entryDate

        if (realEstate.isAvailable) {
            binding.saledateTextView.visibility = GONE
            binding.soldButton.visibility = VISIBLE
            binding.editButton.visibility = VISIBLE
        } else {
            binding.saledateTextView.visibility = VISIBLE
            binding.saledateTextView.text = "Sold : " + realEstate.saleDate
        }

        binding.assignedagentTextView.text = realEstate.assignedAgent
        binding.roomsTextView.text = "Rooms : \n" + "      " + realEstate.room.toString()
        binding.bedroomTextView.text = "Bedrooms : \n" + "      " + realEstate.bedroom.toString()
        binding.bathroomTextView.text = "Bathrooms : \n" + "      " + realEstate.bathroom.toString()

        val list = realEstate.nearbyPOI
        binding.nearbyPOITextView.text = list.joinToString(separator = ", ")


        viewPager = binding.idViewPager

        if (realEstate.photos?.isNotEmpty() == true) {
            imageList = realEstate.photos
            viewPagerAdapter = ViewPagerAdapter(requireContext(), imageList)
            viewPager.adapter = viewPagerAdapter
            tabLayout = binding.tablayout
            tabLayout.setupWithViewPager(viewPager, true)
            tabLayout.touchables?.forEach { it.isEnabled = false }
        } else {
            viewPager.visibility = GONE
            binding.noPicTextView.visibility = VISIBLE
            binding.noPicTextView.text = "No picture has been provided for this property"
        }

        if (realEstate.isAvailable) {
            binding.availabilityTextView.text = "Available"
        } else {
            binding.availabilityTextView.text = "Sold"
        }

        val fullDate = realEstate.entryDate
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date = format.parse(fullDate)
        val day = DateFormat.format("dd", date) as String
        val month = DateFormat.format("MM", date) as String
        val year = DateFormat.format("yyyy", date) as String

        realEstate.id?.let { setupSoldDialog(it, day, month, year) }
    }
}