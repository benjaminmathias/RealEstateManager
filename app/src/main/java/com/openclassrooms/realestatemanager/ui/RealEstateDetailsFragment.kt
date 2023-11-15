package com.openclassrooms.realestatemanager.ui


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsReworkBinding
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.viewmodel.DetailsViewModel
import com.openclassrooms.realestatemanager.viewmodel.RealEstateData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RealEstateDetailsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentDetailsReworkBinding
    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var mMap: GoogleMap

    private var cal: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = Slide(Gravity.END)
        exitTransition = Slide(Gravity.START)

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_details_rework,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observeState()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = this.arguments?.getLong("id")
        val nullValue: Long = 0
        Log.d("id", id.toString())
        if (id == null || id == nullValue) {
            binding.detailsLayout2.visibility = GONE
            binding.emptyDetailsView.root.visibility = VISIBLE
            binding.emptyDetailsView.title.text = "There's no detail to be shown"
            binding.emptyDetailsView.subTitle.text =
                "Please select or create a Real Estate for it to be shown here"
        } else {
            val args = Bundle().apply {
                putLong("id", id)
            }

            binding.editButton.setOnClickListener {
                val currentDestination =
                    this.findNavController().currentDestination == this.findNavController()
                        .findDestination(R.id.fragmentDetails)

                val navHostFragment =
                    requireActivity().supportFragmentManager.primaryNavigationFragment as NavHostFragment
                val navController = navHostFragment.navController

                if (currentDestination) {
                    navController.navigate(R.id.fragmentInput, args)
                }
            }

            val mapView: MapView = binding.detailsMap
            mapView.onCreate(savedInstanceState)
            mapView.onResume()
            mapView.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun setupMap(lat: Double, long: Double) {
        val realEstateLocation = LatLng(lat, long)
        val marker = MarkerOptions().position(realEstateLocation)
        if (this::mMap.isInitialized) {
            mMap.addMarker(marker)
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(lat, long)).zoom(14f).build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailsUiState.collect { uiState ->
                    when (uiState) {
                        is DetailsViewModel.DetailsUiState.Success -> {
                            Log.d("Details", "Observe state success")
                            setupView(uiState.realEstateData)

                            Log.d("Details lat", uiState.realEstateData.lat.toString())
                            Log.d("Details lon", uiState.realEstateData.lon.toString())

                        }

                        is DetailsViewModel.DetailsUiState.Error -> {
                            Log.d("Details", "Observe state error")
                            binding.detailsLayout2.visibility = GONE
                            binding.emptyDetailsView.root.visibility = VISIBLE
                            binding.emptyDetailsView.title.text = "There's no detail to be shown"
                            binding.emptyDetailsView.subTitle.text =
                                "There was an error retrieving the stored property + ${uiState.exception}"
                        }

                        else -> {
                            Log.d("Details", "Observe state else")
                        }
                    }
                }
            }
        }
    }

    private fun setupSoldDialog(id: Long, minDay: String, minMonth: String, minYear: String) {
        binding.soldButton.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Mark this property as sold")
            dialog.setMessage("Click on the `Selected sale date` button to select the sale date of this property")

            dialog.setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "You didn't mark this property as sold",
                    Toast.LENGTH_LONG
                ).show()
            }

            dialog.setNeutralButton("Select sale date") { _, _ ->
                val dateSetListener =
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        cal.set(minYear.toInt(), minMonth.toInt(), minDay.toInt())
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val myFormat = "dd/MM/yyyy"
                        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
                        val saleDate = sdf.format(cal.time)
                        dialog.setMessage("This property will be mark as sold with the corresponding sale date : $saleDate")

                        dialog.setPositiveButton("Confirm") { _, _ ->
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
        val date = cal.time
        val zoneOffset = ZoneOffset.of("+02:00")
        val offsetDateTime = date.toInstant().atOffset(zoneOffset)
        viewModel.setRealEstateAsNoLongerAvailable(offsetDateTime, false, id)
        binding.soldButton.visibility = GONE
    }

    private fun setupView(realEstate: RealEstateData) {

        val utils = Utils()
        binding.priceTextView.text = utils.formatCurrency(realEstate.price, true)

        var isChecked = false
        binding.currencySwitch.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.baseline_attach_money_24
            )
        )

        binding.currencySwitch.setOnClickListener {
            if (isChecked) {
                binding.currencySwitch.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.baseline_attach_money_24
                    )
                )
                binding.priceTextView.text = utils.formatCurrency(realEstate.price, true)
                isChecked = false
            } else {
                binding.currencySwitch.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.baseline_euro_24
                    )
                )
                binding.priceTextView.text =
                    utils.formatCurrency(realEstate.price, false)
                isChecked = true
            }
        }

        viewPager = binding.idViewPager

        if (realEstate.photos.isNotEmpty()) {
            viewPagerAdapter = ViewPagerAdapter(requireContext(), realEstate.photos)
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

        val retrievedDate = realEstate.entryDate
        val day = retrievedDate?.dayOfMonth.toString()
        val month = retrievedDate?.monthValue.toString()
        val year = retrievedDate?.year.toString()

        realEstate.id?.let { setupSoldDialog(it, day, month, year) }

        if (realEstate.lat == 0.0 && realEstate.lon == 0.0){
            Log.d("RealEstate :", "invisible called")
            binding.detailsMap.visibility = GONE
            binding.emptyMapView?.root?.visibility = VISIBLE
            binding.emptyMapView?.title?.text = "Map can't be shown"
            binding.emptyMapView?.subTitle?.text = "Please add a location for this property"
        } else {
            Log.d("RealEstate :", "setup called")
            realEstate.lat?.let {
                realEstate.lon?.let { it1 ->
                    setupMap(
                        it,
                        it1
                    )
                }
            }
        }
    }
}