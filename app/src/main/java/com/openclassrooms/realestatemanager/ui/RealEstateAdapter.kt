package com.openclassrooms.realestatemanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.RealEstate

class RealEstateAdapter(private val realEstate: List<RealEstate>) : RecyclerView.Adapter<RealEstateAdapter.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.realestate_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(realEstate[position])
    }

    override fun getItemCount() = realEstate.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(index: RealEstate) {
            itemView.findViewById<TextView>(R.id.name).text = index.name
            itemView.findViewById<TextView>(R.id.price).text = index.price.toString()
            itemView.findViewById<TextView>(R.id.surface).text = index.surface.toString()
            itemView.findViewById<TextView>(R.id.id).text = index.id.toString()
        }
    }
}