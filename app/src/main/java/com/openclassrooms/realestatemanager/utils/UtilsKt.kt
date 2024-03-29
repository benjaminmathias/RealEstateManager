package com.openclassrooms.realestatemanager.utils


import android.util.Log
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

class UtilsKt {

    fun convertDollarToEuro(dollars: Int): Double {
        return dollars * 0.94
    }

    fun convertEuroToDollar(euros: Int): Double {
        return euros * 1.07
    }

    fun formatCurrency(currencyString: String, dollar: Boolean): String {
        var parsed = 0.0
        if (dollar) {
            val locale: Locale = Locale.US
            val currency = Currency.getInstance(locale)

            val cleanString = currencyString.replace("[${currency.symbol},.]".toRegex(), "")
            if (cleanString.isNotEmpty()) {
                parsed = cleanString.toDouble()
            }

            val maxDigits = NumberFormat.getCurrencyInstance(locale)
            maxDigits.maximumFractionDigits = 0

            return maxDigits.format(parsed / 1)
        } else {
            val locale: Locale = Locale.FRANCE
            val currency = Currency.getInstance(locale)

            Log.d("Currency converter", convertDollarToEuro(currencyString.toInt()).toString())

            val currencyConverted = convertDollarToEuro(currencyString.toInt()).toString()

            val cleanString = currencyConverted.replace("[${currency.symbol},.]".toRegex(), "")
            if (cleanString.isNotEmpty()) {
                parsed = cleanString.toDouble()
            }

            val maxDigits = NumberFormat.getCurrencyInstance(locale)
            maxDigits.maximumFractionDigits = 0

            return maxDigits.format(parsed / 10)
        }
    }

    fun getTodayDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }
}