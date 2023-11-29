package com.openclassrooms.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.utils.UtilsKt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class LoanViewModel @Inject constructor(
) : ViewModel() {

    private var _loanData: MutableStateFlow<LoanData> =
        MutableStateFlow(LoanData())
    val loanData: StateFlow<LoanData> = _loanData

    private var _loanResult: MutableStateFlow<LoanResult> =
        MutableStateFlow(LoanResult())
    val loanResult: StateFlow<LoanResult> = _loanResult

    private val mUtilsKt: UtilsKt = UtilsKt()

    fun computeMortgage() {
        if (!allFieldsAreValid()) return

        val amount = currencyToInt(_loanData.value.amount)
        val rate = _loanData.value.rate.toDouble() / 100
        val duration = _loanData.value.duration.toInt() * 12

        val loanMonthlyAmount = ((amount * rate) / 12) / (1 - (1 + ((rate) / 12)).pow(-duration))
        val removedDecimals = loanMonthlyAmount.toString().substringBefore(".")
        val formattedAmount = mUtilsKt.formatCurrency(removedDecimals, true)

        val amountPerMonth = "$formattedAmount per months"
        val totalDuration = "for a total duration of $duration months."

        val totalAmount = loanMonthlyAmount * duration
        val totalFormat = totalAmount.toString().substringBefore(".")
        val formattedTotalAmount = mUtilsKt.formatCurrency(totalFormat, true)
        val totalAmountText = "Total value : $formattedTotalAmount"

        _loanResult.value = LoanResult(amountPerMonth, totalDuration, totalAmountText )
    }

    private fun allFieldsAreValid(): Boolean {
        if (_loanData.value.amount == "") return false
        if (currencyToInt(_loanData.value.amount) < 10000 ||
            currencyToInt(_loanData.value.amount) > 1000000 ||
            _loanData.value.duration.isEmpty() ||
            _loanData.value.rate.isEmpty()
        ) {
            return false
        }
        return true
    }

    private fun currencyToInt(price: String): Int {
        return price
            .replace("$", "")
            .replace(".", "")
            .replace(",", "")
            .toInt()
    }

    data class LoanData(
        var amount: String = "",
        var rate: String = "",
        var duration: String = ""
    )

    data class LoanResult(
        val monthlyPayment: String = "",
        val totalDuration: String = "",
        val totalAmount: String = "",
    )
}