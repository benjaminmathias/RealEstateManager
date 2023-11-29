package com.openclassrooms.realestatemanager.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.openclassrooms.realestatemanager.utils.UtilsKt
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


val utilsKt = UtilsKt()

@BindingAdapter("android:bindDate")
fun bindDate(view: TextView, date: OffsetDateTime?) {
    if (date == null) {
        view.text = "../../...."
    } else {
        val format = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        view.text = format
    }
}

