package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.PhotoItem
import com.openclassrooms.realestatemanager.databinding.FragmentRealEstateEditBinding
import com.openclassrooms.realestatemanager.viewmodel.NewRealEstateViewModel
import com.openclassrooms.realestatemanager.viewmodel.RealEstateUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentAdd : Fragment(), PhotoAdapter.ItemClickListener {
    private lateinit var binding: FragmentRealEstateEditBinding
    private lateinit var recyclerView: RecyclerView

    lateinit var addRealEstateButton: Button
    private lateinit var addPhotoButton: Button

    var cal: Calendar = Calendar.getInstance()

    private val viewModel: NewRealEstateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_real_estate_edit,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupAutocompletes()

        activity?.title = "Create a new Real Estate"
        binding.entrydateTextView.text = "../../...."
        addPhotoButton = binding.mediaAddButton
        addRealEstateButton = binding.submitButton
        addRealEstateButton.text = "Add"
        setupEntryDate(binding.entrydateTextView)

        // PHOTO //
        // We setup our photo recyclerview here since registerForActivityResult needs to be called in onCreateView
        recyclerView = binding.inputRecyclerView
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = PhotoAdapter(viewModel.realEstateDataFlow.value.photos, this)
        recyclerView.adapter = adapter

        // TODO : observe photos
        // viewModel.realEstateDataFlow.col

       /* if (viewModel.realEstateDataFlow.value.photos.isNotEmpty()) {
            adapter.updateData(viewModel.realEstateDataFlow.value.photos)
        }

        observePhotos()*/

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
                        viewModel.realEstateDataFlow.value.photos.add(photo)
                        Log.d("PhotoItem created", "uri : " + photo.uri)
                    }
                   adapter.updateData(viewModel.realEstateDataFlow.value.photos)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // CHIP //
        // Retrieve chip type single value with a listener then update our type stored in viewmodel
        /* binding.typeChipGroup.setOnCheckedStateChangeListener { chipGroup, _ ->
            viewModel.realEstateData.type = chipGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() }
                .first()
        }*/

        // Retrieve multiple chip value if checked to update our nearByPoi list in viewmodel
        binding.poiChipGroup.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            viewModel.realEstateDataFlow.value.nearbyPOI = binding.poiChipGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() }
                .toList()
        }

        binding.cancelButton.setOnClickListener {
            requireView().findNavController().navigate(R.id.fragmentList)
        }

        observeState()

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        // setupAutocompletes()
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
        builder.setMessage("Add a short description, if there's already one, it will be shown (30 characters max)")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val editText = EditText(requireContext())
        val photoList = viewModel.realEstateDataFlow.value.photos

        if (photoList[position].photoDescription.isEmpty()) {
            editText.hint = "Add your description here"
        } else {
            editText.hint = photoList[position].photoDescription
        }

        editText.filters = arrayOf(InputFilter.LengthFilter(30))

        layout.addView(editText)

        val param = editText.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(50, 0, 50, 0)
        editText.layoutParams = param

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

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is RealEstateUiState.RealEstateDataUi ->
                            Toast.makeText(
                                requireContext(),
                                "Create a new property",
                                Toast.LENGTH_SHORT
                            ).show()

                        is RealEstateUiState.Default ->
                            Toast.makeText(
                                requireContext(),
                                "Edit property",
                                Toast.LENGTH_SHORT
                            ).show()

                        is RealEstateUiState.Success ->
                            notifyRealEstateIsCreated()

                        is RealEstateUiState.Error ->
                            Toast.makeText(
                                requireContext(),
                                "All fields needs to be filled",
                                Toast.LENGTH_LONG
                            ).show()
                    }
                }
            }
        }
    }

   /* private fun observePhotos() {

        var photoList: MutableList<PhotoItem> = mutableListOf()

        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = PhotoAdapter(viewModel.realEstateDataFlow.value.photos, this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.realEstateDataFlow.collect { data ->
                    when (data) {
                        data -> photoList = viewModel.realEstateDataFlow.value.photos
                    }
                }
            }
        }
        adapter.updateData(photoList)
        adapter.notifyDataSetChanged()
    }*/

    private fun notifyRealEstateIsCreated() {
        Toast.makeText(requireContext(), "New property successfully created ! ", Toast.LENGTH_LONG)
            .show()
        requireView().findNavController().navigate(R.id.fragmentList)
    }

    private fun confirmDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
        builder.setMessage("Once this property is added, it can be edited but cannot be deleted, are you sure that you want to add this property ?")

        builder.setPositiveButton("Confirm") { _, _ ->

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
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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
}