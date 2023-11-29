package com.openclassrooms.realestatemanager.ui.input

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.RealEstatePhoto


class PhotoAdapter(
    private var photoList: MutableList<RealEstatePhoto>, private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return ViewHolder(view)
    }

    private var onClickListener: ItemClickListener? = null

    fun setOnClickListener(onClickListener: ItemClickListener) {
        this.onClickListener = onClickListener
    }

    interface ItemClickListener {
        fun onClick(position: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeItem(position: Int) {
        photoList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(realEstatePhotoListNew: MutableList<RealEstatePhoto>) {
        this.photoList = realEstatePhotoListNew
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

         val currentItem = photoList[position]

        if (currentItem.photoDescription.isNotEmpty()) {
            holder.textview.text = currentItem.photoDescription
        }

        if (currentItem.uri.isNotEmpty()) {
            Glide.with(holder.imageView)
                .load(currentItem.uri)
                .centerCrop()
                .into(holder.imageView)
        }

        holder.imageView.setOnClickListener {
            itemClickListener.onClick(position)
        }

        if (photoList.isNotEmpty() && itemCount != 0) {
            holder.button.setOnClickListener {
                removeItem(position)
            }
        }
    }

    override fun getItemCount() = photoList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.photo_item_image_view)
        val button: ImageButton = itemView.findViewById(R.id.photo_item_button)
        val textview: TextView = itemView.findViewById(R.id.photo_item_text_view)
    }
}