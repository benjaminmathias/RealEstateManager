package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.databinding.FilterDialogLayoutBinding
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@AndroidEntryPoint
class FilterDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FilterDialogLayoutBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterDialogLayoutBinding.inflate(layoutInflater, container, false)

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
                }
            }
        })

        binding.submitButtonDialog.setOnClickListener {

            // Get all inputs
            val priceMin = binding.priceMinRange.text.toString().ifEmpty { null }
            val priceMax = binding.priceMaxRange.text.toString().ifEmpty { null }
            val surfaceMin = binding.surfaceMinRange.text.toString().ifEmpty { null }
            val surfaceMax = binding.surfaceMaxRange.text.toString().ifEmpty { null }
            val location = binding.locationEditText.text.toString().ifEmpty { null }
            val room = binding.roomAmount.text.toString().ifEmpty { null }
            val bedroom = binding.bedroomAmount.text.toString().ifEmpty { null }
            val bathroom = binding.bathroomAmount.text.toString().ifEmpty { null }

            val isAvailable: Boolean? = if (binding.checkboxYes.isChecked) {
                true
            } else if (binding.checkboxNo.isChecked) {
                false
            } else {
                null
            }

            Log.d("Available value", isAvailable.toString())

            val nearbyPoiList = binding.poiChipGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() }
            
            val typeName = binding.typeChipGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() }
                .toList()
                .ifEmpty { null }?.get(0)

            Log.d("Filter", "" + typeName)

            val requestOk: Boolean =
                allFieldsAreEmpty(isAvailable, typeName, nearbyPoiList.toList().ifEmpty { null })

            if (requestOk) {
                Log.d("Filter", "Empty fields")
                Toast.makeText(requireContext(), "You must select at least one filter !", Toast.LENGTH_LONG).show()
            } else {
                viewModel.filterList(
                    type = typeName,
                    priceMin = currencyToString(priceMin),
                    priceMax = currencyToString(priceMax),
                    surfaceMin = surfaceMin,
                    surfaceMax = surfaceMax,
                    room = room,
                    bedroom = bedroom,
                    bathroom = bathroom,
                    location = location,
                    nearbyPOI = nearbyPoiList.toList().ifEmpty { null }
                )
                Log.d("Filter", "Filters applied")
                dismiss()
            }
        }

        binding.cancelButtonDialog.setOnClickListener()
        {
            dismiss()
        }

        return binding.root
    }

    private fun allFieldsAreEmpty(
        isAvailable: Boolean?,
        typeName: String?,
        nearbyPOIList: List<String>?
    ): Boolean {
        if (isAvailable == null) {
            if (nearbyPOIList == null) {
                if (typeName == null) {
                    return binding.priceMinRange.text.toString().isEmpty() &&
                            binding.priceMaxRange.text.toString().isEmpty() &&
                            binding.surfaceMinRange.text.toString().isEmpty() &&
                            binding.surfaceMaxRange.text.toString().isEmpty() &&
                            binding.locationEditText.text.toString().isEmpty() &&
                            binding.roomAmount.text.toString().isEmpty() &&
                            binding.bedroomAmount.text.toString().isEmpty() &&
                            binding.bathroomAmount.text.toString().isEmpty()
                }
                return false
            }
            return false
        }
        return false
    }

    private fun currencyToString(price: String?) : String?{
        if (price != null) {
            return price
                .replace("$", "")
                .replace(".", "")
                .replace(",", "")
        }
        return null
    }
}