package com.openclassrooms.realestatemanager.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.openclassrooms.realestatemanager.utils.Utils
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class DateBindingAdapter {

    companion object {

        val utils = Utils()

        @JvmStatic
        @BindingAdapter("android:bindDate")
        fun setDate(view: TextView, date: OffsetDateTime?) {
            if (date == null) {
                view.text = "../../...."
            } else {
                val format = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                view.text = format
            }
        }
    }
}