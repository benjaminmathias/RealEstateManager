package com.openclassrooms.realestatemanager.ui.details

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.openclassrooms.realestatemanager.R
import java.util.Locale

// Package function to avoid build warnings when compiling since a companion object is not a singleton and might emit build warnings
@BindingAdapter("bindValueOrNull")
fun bindValueOrNull(view: TextView, value: String?) {
    if (value.equals("null")) {
        view.text = "-"
    } else {
        view.text = value
    }
}

@BindingAdapter("bindDescription")
fun bindDescription(view: TextView, value: String?) {
    if (value.isNullOrEmpty()) {
        view.text = view.resources.getString(R.string.no_description_adapter)
    } else {
        view.text = value
    }
}

@BindingAdapter("bindLocation")
fun bindLocation(view: TextView, value: String?) {
    if (value.isNullOrEmpty()) {
        view.text = view.resources.getString(R.string.no_location_adapter)
    } else {
        view.text = value
    }
}


@BindingAdapter("bindPointOfInterest")
fun bindPointOfInterest(view: TextView, value: List<String>?) {
    if (value.isNullOrEmpty()) {
        view.text = view.resources.getString(R.string.no_poi_adapter)
    } else {
        view.text = value.joinToString(separator = ", ") { it.lowercase().capitalized() }
    }
}


@BindingAdapter("bindCapitalize")
fun bindCapitalize(view: TextView, value: String?) {
    if (value.isNullOrEmpty()) {
        view.text = "-"
    } else {
        view.text = value.lowercase().capitalized()
    }
}


@BindingAdapter("setVisibleWhenTrue")
fun setVisibleWhenTrue(view: TextView, value: Boolean?) {
    if (value == true) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}


@BindingAdapter("setVisibleWhenFalse")
fun setVisibleWhenFalse(view: TextView, value: Boolean?) {
    if (value == true) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
    }
}

@BindingAdapter("bindAvailable")
fun bindAvailable(view: TextView, value : Boolean?){

    val context = view.context
    if (value == true){
        view.text = view.resources.getString(R.string.item_on_sale)
        view.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.available_color))
    } else {
        view.text = view.resources.getString(R.string.item_sold)
        view.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray))
    }
}

private fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}
