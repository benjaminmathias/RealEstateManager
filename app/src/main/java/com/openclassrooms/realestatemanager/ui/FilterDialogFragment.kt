package com.openclassrooms.realestatemanager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FilterDialogLayoutBinding
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Currency
import java.util.Locale


@AndroidEntryPoint
class FilterDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FilterDialogLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()

    private var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.filter_dialog_layout, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.soldDateLayout.visibility = View.GONE

        observeFilters()
        observeDialog()
        setupPriceEditText()
        clearAllViews()

        binding.radioIgnore.setOnClickListener {
            binding.soldDateLayout.visibility = View.GONE
            viewModel.setSoldDateFrom(null)
            viewModel.setSoldDateTo(null)
        }

        binding.radioNo.setOnClickListener {
            binding.soldDateLayout.visibility = View.VISIBLE
        }

        binding.radioYes.setOnClickListener {
            binding.soldDateLayout.visibility = View.GONE
            viewModel.setSoldDateFrom(null)
            viewModel.setSoldDateTo(null)
        }

        binding.closeImageButton.setOnClickListener {
            dialog?.dismiss()
            clearAllViews()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun observeFilters() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.realEstateFilters.collect {
                    setupDatePicker(
                        binding.fromListedDate,
                        FilterDate.LISTED_FROM,
                        null,
                        it.toListedDate
                    )
                    setupDatePicker(
                        binding.toListedDate,
                        FilterDate.LISTED_TO,
                        it.fromListedDate,
                        null
                    )
                    setupDatePicker(binding.fromSoldDate, FilterDate.SOLD_FROM, null, it.toSoldDate)
                    setupDatePicker(binding.toSoldDate, FilterDate.SOLD_TO, it.fromSoldDate, null)
                }
            }
        }
    }

    private fun observeDialog() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.closeDialog.collect {
                    when (it) {
                        true -> dialog?.dismiss()
                        false -> {}
                    }
                }
            }
        }
    }

    private fun setupDatePicker(
        view: TextView,
        filterDate: FilterDate,
        fromDate: OffsetDateTime?,
        toDate: OffsetDateTime?
    ) {

        val dateDialogListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                if (fromDate != null) {
                    cal.set(fromDate.year, fromDate.monthValue, fromDate.dayOfMonth)
                }

                if (toDate != null) {
                    cal.set(toDate.year, toDate.monthValue, toDate.dayOfMonth)
                }

                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val date = cal.time
                val zoneOffset = ZoneOffset.of("+02:00")
                val offsetDateTime = date.toInstant().atOffset(zoneOffset)

                when (filterDate) {
                    FilterDate.LISTED_TO -> viewModel.setListedDateTo(offsetDateTime)
                    FilterDate.SOLD_TO -> viewModel.setSoldDateTo(offsetDateTime)
                    FilterDate.LISTED_FROM -> viewModel.setListedDateFrom(offsetDateTime)
                    FilterDate.SOLD_FROM -> viewModel.setSoldDateFrom(offsetDateTime)
                }
            }

        view.setOnClickListener {
            val picker = DatePickerDialog(
                requireContext(),
                dateDialogListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            if (fromDate != null) {
                cal.set(fromDate.year, fromDate.monthValue - 1, fromDate.dayOfMonth)
                picker.datePicker.minDate = cal.timeInMillis
            }

            if (toDate != null) {
                cal.set(toDate.year, toDate.monthValue - 1, toDate.dayOfMonth)
                picker.datePicker.maxDate = cal.timeInMillis
            }

            picker.show()
        }
    }

    private fun setupPriceEditText(){
        binding.priceMinRange.addTextChangedListener(object : TextWatcher {

            var current = ""

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val stringText = s.toString()

                var parsed = 0.0

                if (stringText != current) {
                    binding.priceMinRange.removeTextChangedListener(this)

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
                    binding.priceMinRange.setText(formatted)
                    binding.priceMinRange.setSelection(formatted.length)
                    binding.priceMinRange.addTextChangedListener(this)

                    if (formatted == "$0"){
                        binding.priceMinRange.removeTextChangedListener(this)
                        binding.priceMinRange.setText("")
                        binding.priceMinRange.addTextChangedListener(this)
                    }
                }
            }
        })

        binding.priceMaxRange.addTextChangedListener(object : TextWatcher {

            var current = ""

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val stringText = s.toString()

                var parsed = 0.0

                if (stringText != current) {
                    binding.priceMaxRange.removeTextChangedListener(this)

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
                    binding.priceMaxRange.setText(formatted)
                    binding.priceMaxRange.setSelection(formatted.length)
                    binding.priceMaxRange.addTextChangedListener(this)

                    if (formatted == "$0"){
                        binding.priceMaxRange.removeTextChangedListener(this)
                        binding.priceMaxRange.setText("")
                        binding.priceMaxRange.addTextChangedListener(this)
                    }
                }
            }
        })
    }

    private fun clearAllViews(){
        binding.cancelButtonDialog.setOnClickListener {
            binding.surfaceMinRange.text?.clear()
            binding.surfaceMaxRange.text?.clear()
            binding.locationEditText.text?.clear()
            binding.radioGroup.check(R.id.radio_ignore)
            binding.soldDateLayout.visibility = View.GONE
            binding.typeChipGroup.clearCheck()
            binding.poiChipGroup.clearCheck()
            viewModel.clearData()
            // android:onClick="@{() -> viewModel.clearData()}"
        }
    }
}

enum class FilterDate {
    LISTED_FROM, LISTED_TO, SOLD_FROM, SOLD_TO
}
