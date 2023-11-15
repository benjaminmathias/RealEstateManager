package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentRealEstateEditBinding
import com.openclassrooms.realestatemanager.model.data.RealEstatePhoto
import com.openclassrooms.realestatemanager.viewmodel.NewEditRealEstateViewModel
import com.openclassrooms.realestatemanager.viewmodel.RealEstateUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.time.ZoneOffset
import java.util.*

@AndroidEntryPoint
class RealEstateInputFragment : Fragment(), PhotoAdapter.ItemClickListener {
    private lateinit var binding: FragmentRealEstateEditBinding
    private lateinit var recyclerView: RecyclerView

    private lateinit var uri: Uri

    private lateinit var addRealEstateButton: Button
    private lateinit var addPhotoButton: Button

    private var cal: Calendar = Calendar.getInstance()

    private val viewModel: NewEditRealEstateViewModel by viewModels()

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
        observeState()

        activity?.title = "Create a new Real Estate"
        addPhotoButton = binding.mediaAddButton
        addRealEstateButton = binding.submitButton
        setupEntryDate(binding.entrydateTextView)

        // Currency format, dollars only since we ask for $ price
        var current = ""

        binding.priceEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val stringText = s.toString()

                var parsed = 0.0

                if (stringText != current) {
                    binding.priceEditText.removeTextChangedListener(this)

                    val locale: Locale = Locale.US
                    val currency = Currency.getInstance(locale)

                    val cleanString = stringText.replace("[${currency.symbol},.]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        parsed = cleanString.toDouble()
                    }

                    val maxDigits = NumberFormat.getCurrencyInstance(locale)
                    maxDigits.maximumFractionDigits = 0

                    val formatted = maxDigits.format(parsed / 1)

                    current = formatted
                    binding.priceEditText.setText(formatted)
                    binding.priceEditText.setSelection(formatted.length)
                    binding.priceEditText.addTextChangedListener(this)
                }
            }
        })

        // PHOTO //
        // We setup our photo recyclerview here since registerForActivityResult needs to be called in onCreateView
        recyclerView = binding.inputRecyclerView

        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = PhotoAdapter(viewModel.realEstateDataFlow.value.photos, this)
        recyclerView.adapter = adapter

        observePhotos(adapter)

        // Setup photoPicker : needs to be called here; if not app will crash - activityResult needs to be
        // called in the actual fragment - NOT in a click listener.
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                if (uris.isNotEmpty()) {
                    Log.d("PhotoPicker", "Selected URI: ${uris.size}")
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val count = uris.size
                    for (i in 0 until count) {
                        val imageUri = uris[i]
                        context?.contentResolver?.takePersistableUriPermission(imageUri, flag)
                        val photo = RealEstatePhoto(
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

        // Setup TakePicture : needs also to be called here - same reason as above.
        val takePicture =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
                if (isSaved) {
                    val photo = RealEstatePhoto(
                        uri.toString(),
                        ""
                    )
                    viewModel.realEstateDataFlow.value.photos.add(photo)
                }
                adapter.updateData(viewModel.realEstateDataFlow.value.photos)
            }

        // Open BottomSheetDialog where the user can pick existing photos or a new ones
        addPhotoButton.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            val view = layoutInflater.inflate(R.layout.image_selection_bottom_sheet, null)

            val cameraButton = view.findViewById<TextView>(R.id.camera_text_view)
            val galleryButton = view.findViewById<TextView>(R.id.gallery_text_view)

            // Handle pictures taken from camera
            cameraButton.setOnClickListener {
                val photoFile = File.createTempFile(
                    "IMG_",
                    ".jpg",
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )

                uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile
                )

                takePicture.launch(uri)
                dialog.cancel()
            }

            // Handle pictures picked from gallery
            galleryButton.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                dialog.cancel()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        binding.cancelButton.setOnClickListener {
            requireView().findNavController().navigate(R.id.fragmentList)
        }

        return binding.root
    }

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

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is RealEstateUiState.RealEstateDataUi -> {
                            addRealEstateButton.text = "Add"
                            activity?.title = "Create a new Real Estate"
                            Toast.makeText(
                                requireContext(),
                                "Create a new property",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is RealEstateUiState.Default -> {
                            addRealEstateButton.text = "Edit"
                            activity?.title = "Edit existing property"
                            Toast.makeText(
                                requireContext(),
                                "Edit property",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.locationTextView.text = "Stored location"
                            binding
                        }

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

    private fun observePhotos(adapter: PhotoAdapter) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.realEstateDataFlow.collect { data ->
                    when (data) {
                        data -> {
                            adapter.updateData(viewModel.realEstateDataFlow.value.photos)
                        }
                    }
                }
            }
        }
    }

    private fun notifyRealEstateIsCreated() {
        Toast.makeText(requireContext(), "Success ! ", Toast.LENGTH_LONG).show()
        requireView().findNavController().navigate(R.id.fragmentList)
    }

    private fun setupEntryDate(entryDateTextView: TextView) {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val timeInMillis = cal.time
                Log.d("Time", timeInMillis.toString())
                updateEntryDateView()
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

    private fun updateEntryDateView() {
        val date = cal.time
        val zoneOffset = ZoneOffset.of("+02:00")
        val offsetDateTime = date.toInstant().atOffset(zoneOffset)
        viewModel.updateEntryDate(offsetDateTime)
    }
}