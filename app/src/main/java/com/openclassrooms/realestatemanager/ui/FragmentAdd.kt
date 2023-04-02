package com.openclassrooms.realestatemanager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
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
    private lateinit var saleDateButton: Button

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
        saleDateButton = binding.includedInputLayout.saledateButton

        setupSwitch()
        setupEntryButton()
        setupSaleButton()

        // Setup photoPicker : needs to be called here; if not app will crash - activityResult needs to be
        // called in the actual fragment - NOT in a click listener
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                if (uris.isNotEmpty()) {
                    Log.d("PhotoPicker", "Selected URI: ${uris.size}")
                    binding.includedInputLayout.mediaImageView.setImageURI(uris[0])
                    // Set tag to retrieve Uri
                    binding.includedInputLayout.mediaImageView.tag = uris[0]
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
        setupSpinners()

    }

    private fun addRealEstate() {
        addRealEstateButton.setOnClickListener {

            // TODO : change spinner to autocomplete

            val type = binding.includedInputLayout.typeSpinner.selectedItem
            val assignedAgent = binding.includedInputLayout.assignedagentSpinner.selectedItem
            val surface = binding.includedInputLayout.surfaceEditText.text.toString()
            val price = binding.includedInputLayout.priceEditText.text.toString()
            val room = binding.includedInputLayout.roomEditText.text.toString()
            val bedroom = binding.includedInputLayout.bedroomEditText.text.toString()
            val bathroom = binding.includedInputLayout.bathroomEditText.text.toString()
            val entryDate = binding.includedInputLayout.entrydateButton.text.toString()
            val isChecked = binding.includedInputLayout.statusSwitch.isChecked


            val path: Any? = binding.includedInputLayout.mediaImageView.tag
            val photoList = ArrayList<PhotoItem>()
            if (binding.includedInputLayout.mediaImageView.drawable != null) {


                val photo = PhotoItem(
                    path.toString(),
                    "test"
                )

                photoList.add(photo)
            }

            // TODO : add a textview for each button to set the date on it, with a default null value for saleDate
            val saleDate = if (!isChecked) {
                binding.includedInputLayout.entrydateButton.text.toString()
            } else {
                null
            }

            // TODO : rename isSold to isAvailable in model to match the switch button boolean value
          /*  if (surface.isNotEmpty() && price.isNotEmpty() && room.isNotEmpty()) {
                viewModel.addRealEstate(
                    RealEstateEntity(
                        type = type.toString(),
                        surface = surface.toInt(),
                        price = price.toInt(),
                        description = binding.includedInputLayout.descriptionEditText.text.toString(),
                        address = binding.includedInputLayout.locationEditText.text.toString(),
                        isAvailable = isChecked,
                        entryDate = entryDate,
                        saleDate = saleDate,
                        assignedAgent = assignedAgent.toString(),
                        room = room.toInt(),
                        bedroom = bedroom.toInt(),
                        bathroom = bathroom.toInt()
                    )
                )
            }*/

            if (surface.isNotEmpty() && price.isNotEmpty() && room.isNotEmpty()) {
                viewModel.addRealEstateDTO(
                    RealEstate(
                        type = type.toString(),
                        surface = surface.toInt(),
                        price = price.toInt(),
                        description = binding.includedInputLayout.descriptionEditText.text.toString(),
                        address = binding.includedInputLayout.locationEditText.text.toString(),
                        photos = photoList,
                        isAvailable = isChecked,
                        entryDate = entryDate,
                        saleDate = saleDate,
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

    private fun setupSwitch() {
        binding.includedInputLayout.statusSwitch.isChecked = true

        binding.includedInputLayout.statusSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.includedInputLayout.saledateButton.visibility = GONE
                binding.includedInputLayout.saledateTitleTextView.visibility = GONE
            } else {
                binding.includedInputLayout.saledateButton.visibility = VISIBLE
                binding.includedInputLayout.saledateTitleTextView.visibility = VISIBLE
            }
        }
    }

    private fun setupSpinners() {
        binding.includedInputLayout.typeSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.type_array, android.R.layout.simple_spinner_item
        )

        binding.includedInputLayout.assignedagentSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.agent_array, android.R.layout.simple_spinner_item
        )
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
        binding.includedInputLayout.entrydateButton.text = sdf.format(cal.time)
    }

    private fun setupSaleButton() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateSaleDateView()
            }

        saleDateButton.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateSaleDateView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
        binding.includedInputLayout.saledateButton.text = sdf.format(cal.time)
    }
}