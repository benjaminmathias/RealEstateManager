package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.data.RealEstate
import com.openclassrooms.realestatemanager.utils.Utils
import java.util.Locale

class RealEstateAdapter(
    private var realEstateList: MutableList<RealEstate>
) : RecyclerView.Adapter<RealEstateAdapter.ViewHolder>()  {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.realestate_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(realEstateNewList: List<RealEstate>){
        this.realEstateList.clear()
        this.realEstateList.addAll(realEstateNewList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val context = holder.itemView.context

        val currentItem = realEstateList[position]
        val utils = Utils()

        holder.typeText.text = currentItem.type.lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        holder.priceText.text = utils.formatCurrency(currentItem.price.toString(), true)
        holder.locationText.text = currentItem.address

        if (currentItem.isAvailable) {
            holder.availabilityText.text = "ON SALE"
            holder.availabilityText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.available_color))
        } else {
            holder.availabilityText.text = "SOLD"
            holder.availabilityText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray))
        }

        holder.imgCounterText.text = currentItem.photos?.size.toString()

        if(currentItem.photos.isNullOrEmpty()) {
            holder.imageView.setImageResource(R.drawable.baseline_house_24)
            holder.imageView.visibility = View.GONE
        } else {
            Glide.with(holder.imageView)
                .load(currentItem.photos[0].uri)
                .centerCrop()
                .into(holder.imageView)
        }

        holder.itemView.setOnClickListener{
            if (onClickListener != null){
                onClickListener!!.onClick(position, currentItem)
            }
        }
    }

    override fun getItemCount() = realEstateList.size

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: RealEstate)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeText : TextView = itemView.findViewById(R.id.type)
        val priceText : TextView = itemView.findViewById(R.id.price)
        val locationText : TextView = itemView.findViewById(R.id.location)
        val imageView : ImageView = itemView.findViewById(R.id.image_view)
        val availabilityText : TextView = itemView.findViewById(R.id.availability)
        val imgCounterText : TextView = itemView.findViewById(R.id.image_counter_text_view)
    }
}