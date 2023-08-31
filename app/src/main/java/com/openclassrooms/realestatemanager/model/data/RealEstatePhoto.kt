package com.openclassrooms.realestatemanager.model.data

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

data class RealEstatePhoto(
    val uri: String,
    var photoDescription: String,
)

@BindingAdapter("photo")
fun loadImage(imageView: ImageView, imageURI: String) {
    Glide.with(imageView.context)
        .load(imageURI)
        .centerCrop()
        .into(imageView)
}