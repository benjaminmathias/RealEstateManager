package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PhotoItem
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.databinding.FragmentDetailsTestBinding
import com.openclassrooms.realestatemanager.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentDetails : Fragment() {

    private lateinit var binding: FragmentDetailsTestBinding

    private val viewModel: DetailsViewModel by viewModels()

    private var position = 0

    lateinit var viewPager: ViewPager
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var tabLayout: TabLayout
    lateinit var imageList: List<PhotoItem>
    private lateinit var soldButton: Button
    private var realEstateId = 0

    var cal: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsTestBinding.inflate(layoutInflater, container, false)

        val id = this.arguments?.getLong("id")
        if (id != null) {
            getRealEstate(id)
        }
        Log.d("Details", "Details id = $id")

        activity?.title = "Real Estate details"

        soldButton = binding.soldButton

        // binding.imageSwitcher.setFactory { ImageView(context) }

        updateUI()
        //observeRealEstate()

        return binding.root
    }

    private fun getRealEstate(id: Long) {
        viewModel.getSpecificRealEstateConverted(id)
    }

    private fun updateUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is DetailsViewModel.DetailsUiState.Success ->
                            setupView(uiState.realEstate)
                        else -> {
                            setupEmptyView()
                        }
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
    private fun setupEmptyView() {
        binding.typeTextView.text = "Error"
    }

    private fun setupSoldButton(id: Long) {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateRealEstate(id)
            }

        soldButton.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateRealEstate(id: Long) {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
        val saleDate = sdf.format(cal.time)
        viewModel.updateRealEstate(saleDate, false, id)
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
        } else {
            binding.saledateTextView.visibility = VISIBLE
            binding.saledateTextView.text = "Sold : " + realEstate.saleDate
        }

        binding.assignedagentTextView.text = "Assigned agent : " + realEstate.assignedAgent
        binding.roomsTextView.text = "Rooms : " + realEstate.room.toString()
        binding.bedroomTextView.text = "Bedrooms : " + realEstate.bedroom.toString()
        binding.bathroomTextView.text = "Bathrooms : " + realEstate.bathroom.toString()

        viewPager = binding.idViewPager

        imageList = realEstate.photos!!

        viewPagerAdapter = ViewPagerAdapter(requireContext(), imageList)
        viewPager.adapter = viewPagerAdapter
        tabLayout = binding.tablayout
        tabLayout.setupWithViewPager(viewPager, true)
        tabLayout.touchables?.forEach { it.isEnabled = false }

        /*
        binding.previousButton.setOnClickListener {
            if (position > 0) {
                position--
                binding.imageSwitcher.setImageURI(Uri.parse(realEstate.photos?.get(position)?.uri))
                /*
                Glide.with(binding.imageSwitcher)
                    .load(realEstate.photos?.get(position)?.uri)
                    .centerCrop()
                    .into(binding.imageSwitcher)*/
            } else {
                Toast.makeText(context, "This is the first image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.nextButton.setOnClickListener {
            if (position < realEstate.photos?.size!! - 1) {
                position++
                binding.imageSwitcher.setImageURI(Uri.parse(realEstate.photos[position].uri))
                /*Glide.with(binding.imageSwitcher)
                    .load(realEstate.photos[position].uri)
                    .centerCrop()
                    .into(binding.imageSwitcher)*/
            } else {
                Toast.makeText(context, "This is the last image", Toast.LENGTH_SHORT).show()
            }
        }
*/
        /*
        Glide.with(binding.mediaAddButton)
            .load(realEstate.photos?.get(0)?.uri)
            .centerCrop()
            .into(binding.mediaAddButton)
         */

        if (realEstate.isAvailable) {
            binding.availabilityTextView.text = "Available"
            binding.availabilityTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimaryDark
                )
            )
        } else {
            binding.availabilityTextView.text = "Sold"
            binding.availabilityTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
        }

        realEstate.id?.let { setupSoldButton(it) }
    }
}