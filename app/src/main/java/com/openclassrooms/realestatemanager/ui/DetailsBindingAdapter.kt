package com.openclassrooms.realestatemanager.ui

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.Locale

class DetailsBindingAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("android:bindValueOrNull")
        fun setText(view: TextView, value: String?) {
            if (value.equals("null")) {
                view.text = "-"
            } else {
                view.text = value
            }
        }

        @JvmStatic
        @BindingAdapter("android:bindDescription")
        fun setDescription(view: TextView, value: String?) {
            if (value.isNullOrEmpty()) {
                view.text = "Description hasn't been provided for this property."
            } else {
                view.text = value
            }
        }

        @JvmStatic
        @BindingAdapter("android:bindLocation")
        fun setLocation(view: TextView, value: String?) {
            if (value.isNullOrEmpty()) {
                view.text = "Location hasn't been provided for this property."
            } else {
                view.text = value
            }
        }

        @JvmStatic
        @BindingAdapter("android:bindPointOfInterest")
        fun setPointOfInterest(view: TextView, value: List<String>?) {
            if (value.isNullOrEmpty()) {
                view.text = "Nearby POI hasn't been provided for this property."
            } else {
                view.text = value.joinToString(separator = ", ") { it.lowercase().capitalized() }
            }
        }

        @JvmStatic
        @BindingAdapter("android:bindCapitalize")
        fun setToCapitalize(view: TextView, value: String?){
            if (value.isNullOrEmpty()){
                view.text = "-"
            } else {
                view.text = value.lowercase().capitalized()
            }
        }

        @JvmStatic
        @BindingAdapter("android:setVisibleWhenTrue")
        fun setVisibleWhenTrue(view: TextView, value: Boolean?){
            if (value == true){
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        @JvmStatic
        @BindingAdapter("android:setVisibleWhenFalse")
        fun setVisibleWhenFalse(view: TextView, value: Boolean?){
            if (value == true){
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }



        private fun String.capitalized(): String {
            return this.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }
    }
}