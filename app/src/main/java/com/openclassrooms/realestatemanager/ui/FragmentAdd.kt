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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PhotoItem
import com.openclassrooms.realestatemanager.databinding.FragmentRealEstateEditBinding
import com.openclassrooms.realestatemanager.viewmodel.NewRealEstateViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentAdd : Fragment(), PhotoAdapter.ItemClickListener {
    private lateinit var binding: FragmentRealEstateEditBinding
    private lateinit var recyclerView: RecyclerView

    lateinit var addRealEstateButton: Button
    private lateinit var addPhotoButton: Button

    private var images: ArrayList<Uri?>? = null
    private var description: ArrayList<String?>? = null

    //var photoList = mutableListOf<PhotoItem>()

    // private val adapter = PhotoAdapter(this@FragmentAdd, photoList)

    private var position = 0

    private var isAllFieldsChecked = false

    var cal: Calendar = Calendar.getInstance()

    private val viewModel: NewRealEstateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // val addBinding: FragmentRealEstateEditBinding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_real_estate_edit)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_real_estate_edit,
            container,
            false
        )

        //  binding = DataBindingUtil.setContentView(layoutInflater, container, false)
        // binding = FragmentRealEstateEditBinding.inflate(layoutInflater, container, false)
        // binding.viewModel = viewModel

        activity?.title = "Create a new Real Estate"

        binding.viewModel = viewModel

        addRealEstateButton = binding.submitButton
        addRealEstateButton.text = "Add"
        binding.entrydateTextView.text = "../../...."
        addPhotoButton = binding.mediaAddButton

        setupEntryDate(binding.entrydateTextView)

        images = ArrayList()
        description = ArrayList()

        recyclerView = binding.inputRecyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = PhotoAdapter(viewModel.realEstateData.photos, this)
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
                        viewModel.realEstateData.photos.add(photo)
                        Log.d("PhotoItem created", "uri : " + photo.uri)
                    }
                    adapter.updateData(viewModel.realEstateData.photos)
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
                getTypeChip()
                getPoiChip()
                confirmDialog()
            }
        }

        binding.cancelButton.setOnClickListener {
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

    private fun getTypeChip() {
       //  binding.typeChipGroup.set

        viewModel.getTypeChip(
            binding.typeChipGroup.children
                .toList()
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() }
                .first()
        )
    }

    private fun getPoiChip() {
        viewModel.getPoiChip(
            binding.poiChipGroup.children
                .toList()
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() } as ArrayList<String>
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add an image description")
        builder.setMessage("Add a short description, if there's already one, it will be shown (30 characters max)")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val editText = EditText(requireContext())
        val photoList = viewModel.realEstateData.photos

        if (photoList[position].photoDescription.isEmpty()) {
            editText.hint = "Add your description here"
        } else {
            editText.hint = photoList[position].photoDescription
        }

        editText.filters = arrayOf(InputFilter.LengthFilter(30))
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

    private fun confirmDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
        builder.setMessage("Once this property is added, it can be edited but cannot be deleted, are you sure that you want to add this property ?")

        builder.setPositiveButton("Confirm") { _, _ ->

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
        (binding.assignedagentSpinner as? AutoCompleteTextView)?.setAdapter(
            agentsAdapter
        )

        binding.assignedagentSpinner.setOnClickListener {
            binding.assignedagentSpinner.showDropDown()
        }
    }

    private fun setupEntryDate(entryDateTextView: TextView) {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateEntryDateView(entryDateTextView)
            }

        entryDateTextView.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateEntryDateView(entryDateTextView: TextView) {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
        entryDateTextView.text = sdf.format(cal.time)
    }

    private fun checkAllFields(): Boolean {
        if (!binding.typeChipGroup.isSelectionRequired) {
            Toast.makeText(requireContext(), "You need to select a type !", Toast.LENGTH_LONG)
                .show()
            return false
        }
        if (binding.priceEditText.length() == 0) {
            binding.priceEditText.error = "This field is required"
            return false
        }
        if (binding.surfaceEditText.length() == 0) {
            binding.surfaceEditText.error = "This field is required"
            return false
        }
        if (binding.roomEditText.length() == 0) {
            binding.roomEditText.error = "This field is required"
            return false
        }
        if (binding.bedroomsEditText.length() == 0) {
            binding.bedroomsEditText.error = "This field is required"
            return false
        }
        if (binding.bathroomsEditText.length() == 0) {
            binding.bathroomsEditText.error = "This field is required"
            return false
        }
        if (binding.locationEditText.length() == 0) {
            binding.locationEditText.error = "This field is required"
            return false
        }
        if (binding.descriptionEditText.length() == 0) {
            binding.descriptionEditText.error = "This field is required"
            return false
        }
        if (binding.assignedagentSpinner.text.isEmpty()) {
            binding.assignedagentSpinner.error = "This field is required"
            return false
        }
        return true
    }
}