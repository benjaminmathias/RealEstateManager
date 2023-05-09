package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PhotoItem
import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.viewmodel.NewRealEstateViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentAddOld : Fragment(), PhotoAdapter.ItemClickListener {
    private lateinit var binding: FragmentAddBinding
    private lateinit var recyclerView: RecyclerView

    lateinit var addRealEstateButton: Button
    private lateinit var addPhotoButton: Button

    private var images: ArrayList<Uri?>? = null
    private var description: ArrayList<String?>? = null

    var photoList = mutableListOf<PhotoItem>()

    // private val adapter = PhotoAdapter(this@FragmentAdd, photoList)

    private var position = 0

    var isAllFieldsChecked = false

    var cal: Calendar = Calendar.getInstance()

    private val viewModel: NewRealEstateViewModel by viewModels()

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

        setupEntryDate()

        images = ArrayList()
        description = ArrayList()

        recyclerView = binding.includedInputLayout.inputRecyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = PhotoAdapter(photoList, this)
        recyclerView.adapter = adapter

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
                        val photo = PhotoItem(
                            imageUri.toString(),
                            ""
                        )
                        photoList.add(photo)
                        Log.d("PhotoItem created", "uri : " + photo.uri)
                    }
                    adapter.updateData(photoList)
                    binding.includedInputLayout.mediaDescriptionTipTextView.visibility =
                        View.VISIBLE

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        addRealEstateButton.setOnClickListener {
            isAllFieldsChecked = checkAllFields()
            if (isAllFieldsChecked) {
                confirmDialog()
            }
        }

        binding.includedInputLayout.cancelButton.setOnClickListener {
            requireView().findNavController().navigate(R.id.fragmentList)
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

    // TODO : check for updated non-deprecated methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add an image description")
        builder.setMessage("Add a short description, if there's already one, it will be shown (20 characters max)")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val editText = EditText(requireContext())
        // editText.layoutParams = // TODO: Set padding to 16dp for the edittext

        if (photoList[position].photoDescription.isEmpty()) {
            editText.hint = "Add your description here"
        } else {
            editText.hint = photoList[position].photoDescription
        }

        editText.filters = arrayOf(InputFilter.LengthFilter(20))
        layout.addView(editText)
        builder.setView(layout)

        builder.setPositiveButton("Confirm") { _, _ ->
            photoList[position].photoDescription = editText.text.toString()
            recyclerView.adapter?.notifyDataSetChanged()
        }

        builder.setNegativeButton("Cancel") { _, _ ->
        }

        builder.show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun addRealEstate() {
        val assignedAgent = binding.includedInputLayout.assignedagentSpinner.text
        val surface = binding.includedInputLayout.surfaceEditText.text.toString()
        val price = binding.includedInputLayout.priceEditText.text.toString()
        val room = binding.includedInputLayout.roomEditText.text.toString()
        val bedroom = binding.includedInputLayout.bedroomsEditText.text.toString()
        val bathroom = binding.includedInputLayout.bathroomsEditText.text.toString()
        val entryDate = binding.includedInputLayout.entrydateTextView.text.toString()

        val typeChipGroup = binding.includedInputLayout.typeChipGroup

        val selectedTypeChip = typeChipGroup.children
            .filter { (it as Chip).isChecked }
            .map { (it as Chip).text.toString() }

        val poiChipGroup = binding.includedInputLayout.poiChipGroup

        val selectedPoiChip = poiChipGroup.children
            .filter { (it as Chip).isChecked }
            .map { (it as Chip).text.toString() }

        Log.d("Photo list added", "Number of item :" + photoList.size)

        if (surface.isNotEmpty() && price.isNotEmpty() && room.isNotEmpty() && selectedTypeChip.first()
                .isNotEmpty()
        ) {
            viewModel.addRealEstateDTO(
                RealEstate(
                    type = selectedTypeChip.first(),
                    surface = surface.toInt(),
                    price = price.toInt(),
                    description = binding.includedInputLayout.descriptionEditText.text.toString(),
                    address = binding.includedInputLayout.locationEditText.text.toString(),
                    photos = photoList,
                    nearbyPOI = selectedPoiChip.toList() as ArrayList<String>,
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

    private fun confirmDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
        builder.setMessage("Once this property is added, it can be edited but cannot be deleted, are you sure that you want to add this property ?")

        builder.setPositiveButton("Confirm") { _, _ ->
            addRealEstate()
            requireView().findNavController().navigate(R.id.fragmentList)
        }

        builder.setNegativeButton("Cancel") { _, _ ->
        }

        builder.show()

    }

    private fun setupAutocompletes() {
        // For Agent autocomplete
        val agents = resources.getStringArray(R.array.agent_array)

        val agentsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, agents)
        (binding.includedInputLayout.assignedagentSpinner as? AutoCompleteTextView)?.setAdapter(
            agentsAdapter
        )

        binding.includedInputLayout.assignedagentSpinner.setOnClickListener {
            binding.includedInputLayout.assignedagentSpinner.showDropDown()
        }
    }


    private fun setupEntryDate() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEntryDateView()
            }

        binding.includedInputLayout.entrydateTextView.setOnClickListener {
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

        if (!viewgroup.typeChipGroup.isSelectionRequired) {
            Toast.makeText(requireContext(), "You need to select a type !", Toast.LENGTH_LONG)
                .show()
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