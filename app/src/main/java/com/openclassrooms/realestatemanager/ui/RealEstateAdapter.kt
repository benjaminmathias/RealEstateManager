package com.openclassrooms.realestatemanager.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstate

class RealEstateAdapter(private val fragment: FragmentList, private var realEstateList: List<RealEstate>) : RecyclerView.Adapter<RealEstateAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.realestate_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(realEstateList: List<RealEstate>){
        this.realEstateList = realEstateList
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = realEstateList[position]

        holder.typeText.text = currentItem.type
        holder.priceText.text = "$" + currentItem.price.toString()
        holder.locationText.text = currentItem.address
        //holder.id.text = currentItem.id.toString()

       // holder.imageView.setImageResource(R.drawable.house_default)

        if(realEstateList[position].photos.isNullOrEmpty()) {
            holder.imageView.setImageResource(R.drawable.baseline_house_24)
        } else {
            Glide.with(holder.imageView)
                .load(realEstateList[position].photos?.get(0)?.uri)
                .centerCrop()
                .into(holder.imageView)
        }

        holder.itemView.setOnClickListener{
            val id = realEstateList[position].id

            if (id != null) {
                fragment.onClick(id)
            }
        }

    }

    override fun getItemCount() = realEstateList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val typeText : TextView = itemView.findViewById(R.id.type)
        val priceText : TextView = itemView.findViewById(R.id.price)
        val locationText : TextView = itemView.findViewById(R.id.location)
        // val id : TextView = itemView.findViewById(R.id.id)
        val imageView : ImageView = itemView.findViewById(R.id.image)

    }
}