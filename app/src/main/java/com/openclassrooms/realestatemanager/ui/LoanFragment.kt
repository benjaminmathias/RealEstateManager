package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentLoanBinding
import com.openclassrooms.realestatemanager.utils.InputFilterMinMax
import com.openclassrooms.realestatemanager.viewmodel.LoanViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@AndroidEntryPoint
class LoanFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentLoanBinding
    private lateinit var submitLoanButton : Button

    private val viewModel: LoanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_loan,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this@LoanFragment

        activity?.title = "RealEstateManager"

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        submitLoanButton = binding.submitLoanButton

        binding.durationEditText.filters = arrayOf(InputFilterMinMax(1,25))
        binding.rateEditText.filters = arrayOf(InputFilterMinMax(0,30))

        var current = ""
        binding.amountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val stringText = s.toString()

                var parsed = 0.0

                if (stringText != current) {
                    binding.amountEditText.removeTextChangedListener(this)

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
                    binding.amountEditText.setText(formatted)
                    binding.amountEditText.setSelection(formatted.length)
                    binding.amountEditText.addTextChangedListener(this)
                }
            }
        })

        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}