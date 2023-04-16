package com.openclassrooms.realestatemanager.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PhotoItem
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.viewmodel.AddViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentAdd : Fragment() {
    private lateinit var binding: FragmentAddBinding

    lateinit var addRealEstateButton: Button
    private lateinit var addPhotoButton: Button
    private lateinit var entryDateButton: Button

    private var images: ArrayList<Uri?>? = null
    private var position = 0

    var isAllFieldsChecked = false

    var cal: Calendar = Calendar.getInstance()

    private val viewModel: AddViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        activity?.title = "Create a new Real Estate"

        addRealEstateButton = binding.includedInputLayout.submitButton
        addRealEstateButton.text = "Add"

        addPhotoButton = binding.includedInputLayout.mediaAddButton
        entryDateButton = binding.includedInputLayout.entrydateButton

        setupEntryButton()

        images = ArrayList()

        binding.includedInputLayout.imageSwitcher.setFactory { ImageView(context) }

        // Setup photoPicker : needs to be called here; if not app will crash - activityResult needs to be
        // called in the actual fragment - NOT in a click listener
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                if (uris.isNotEmpty()) {
                    Log.d("PhotoPicker", "Selected URI: ${uris.size}")
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val count = uris.size
                    for (i in 0 until count) {
                        val imageUri = uris[i]
                        context?.contentResolver?.takePersistableUriPermission(imageUri, flag)
                        images!!.add(imageUri)
                    }
                    binding.includedInputLayout.imageSwitcher.visibility = VISIBLE
                    binding.includedInputLayout.imageSwitcher.setImageURI(images!![0])

                    if (images!!.size > 1) {
                        binding.includedInputLayout.previousButton.visibility = VISIBLE
                        binding.includedInputLayout.nextButton.visibility = VISIBLE
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.includedInputLayout.previousButton.setOnClickListener {
            if (position > 0) {
                position--
                binding.includedInputLayout.imageSwitcher.setImageURI(images!![position])
            } else {
                Toast.makeText(context, "This is the first image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.includedInputLayout.nextButton.setOnClickListener {
            if (position < images!!.size - 1) {
                position++
                binding.includedInputLayout.imageSwitcher.setImageURI(images!![position])
            } else {
                Toast.makeText(context, "This is the last image", Toast.LENGTH_SHORT).show()
            }
        }

        addRealEstateButton.setOnClickListener {
            addRealEstate()
        }
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupAutocompletes()
    }

    private fun addRealEstate() {
        addRealEstateButton.setOnClickListener {
            isAllFieldsChecked = checkAllFields()

            if (isAllFieldsChecked) {
                val type = binding.includedInputLayout.typeSpinner.text
                val assignedAgent = binding.includedInputLayout.assignedagentSpinner.text
                val surface = binding.includedInputLayout.surfaceEditText.text.toString()
                val price = binding.includedInputLayout.priceEditText.text.toString()
                val room = binding.includedInputLayout.roomEditText.text.toString()
                val bedroom = binding.includedInputLayout.bedroomsEditText.text.toString()
                val bathroom = binding.includedInputLayout.bathroomsEditText.text.toString()
                val entryDate = binding.includedInputLayout.entrydateTextView.text.toString()

                val poiList = ArrayList<String>()
                val healthcare = binding.includedInputLayout.healthcareCheckbox.isChecked
                val school = binding.includedInputLayout.schoolCheckbox.isChecked
                val park = binding.includedInputLayout.parkCheckbox.isChecked
                val restaurant = binding.includedInputLayout.restaurantCheckbox.isChecked
                val shopping = binding.includedInputLayout.shoppingCheckbox.isChecked

                if (healthcare) {
                    poiList.add("Healthcare")
                } else if (school) {
                    poiList.add("School")
                } else if (park) {
                    poiList.add("Park")
                } else if (restaurant) {
                    poiList.add("Restaurant")
                } else if (shopping) {
                    poiList.add("Shopping")
                }

                val photoList = ArrayList<PhotoItem>()
                if (images?.isNotEmpty() == true) {
                    val count = images!!.size
                    for (i in 0 until count) {
                        val photo = PhotoItem(
                            images!![i].toString(),
                            "test"
                        )
                        photoList.add(photo)
                    }
                }

                Log.d("Photo list added", "Number of item :" + photoList.size)

                if (surface.isNotEmpty() && price.isNotEmpty() && room.isNotEmpty()) {
                    viewModel.addRealEstateDTO(
                        RealEstate(
                            type = type.toString(),
                            surface = surface.toInt(),
                            price = price.toInt(),
                            description = binding.includedInputLayout.descriptionEditText.text.toString(),
                            address = binding.includedInputLayout.locationEditText.text.toString(),
                            photos = photoList,
                            //   nearbyPOI = poiList,
                            isAvailable = true,
                            entryDate = entryDate,
                            saleDate = null,
                            assignedAgent = assignedAgent.toString(),
                            room = room.toInt(),
                            bedroom = bedroom.toInt(),
                            bathroom = bathroom.toInt(),
                            id = null
                        )
                    )
                }
                Toast.makeText(context, "RealEstate added !", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupAutocompletes() {
        // For Types autocomplete
        val types = resources.getStringArray(R.array.type_array)

        val typesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        (binding.includedInputLayout.typeSpinner as? AutoCompleteTextView)?.setAdapter(typesAdapter)

        binding.includedInputLayout.typeSpinner.setOnClickListener {
            binding.includedInputLayout.typeSpinner.showDropDown()
        }

        // For Agent autocomplete
        val agents = resources.getStringArray(R.array.agent_array)

        val agentsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, agents)
        (binding.includedInputLayout.assignedagentSpinner as? AutoCompleteTextView)?.setAdapter(
            agentsAdapter
        )

        binding.includedInputLayout.assignedagentSpinner.setOnClickListener {
            binding.includedInputLayout.assignedagentSpinner.showDropDown()
        }
    }


    private fun setupEntryButton() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEntryDateView()
            }

        entryDateButton.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateEntryDateView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
        binding.includedInputLayout.entrydateTextView.text = sdf.format(cal.time)
    }

    private fun checkAllFields(): Boolean {
        val viewgroup = binding.includedInputLayout
        if (viewgroup.typeSpinner.text.isEmpty()) {
            viewgroup.typeSpinner.error = "This field is required"
            return false
        }
        if (viewgroup.priceEditText.length() == 0) {
            viewgroup.priceEditText.error = "This field is required"
            return false
        }
        if (viewgroup.surfaceEditText.length() == 0) {
            viewgroup.surfaceEditText.error = "This field is required"
            return false
        }
        if (viewgroup.roomEditText.length() == 0) {
            viewgroup.roomEditText.error = "This field is required"
            return false
        }
        if (viewgroup.bedroomsEditText.length() == 0) {
            viewgroup.bedroomsEditText.error = "This field is required"
            return false
        }
        if (viewgroup.bathroomsEditText.length() == 0) {
            viewgroup.bathroomsEditText.error = "This field is required"
            return false
        }
        if (viewgroup.locationEditText.length() == 0) {
            viewgroup.locationEditText.error = "This field is required"
            return false
        }
        if (viewgroup.descriptionEditText.length() == 0) {
            viewgroup.descriptionEditText.error = "This field is required"
            return false
        }
        if (viewgroup.assignedagentSpinner.text.isEmpty()) {
            viewgroup.assignedagentSpinner.error = "This field is required"
            return false
        }
        return true
    }
}