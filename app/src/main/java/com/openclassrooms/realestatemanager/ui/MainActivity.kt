package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.View

import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.openclassrooms.realestatemanager.data.RealEstate
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var realestateRecyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        realestateRecyclerView = binding.realestateList
        realestateRecyclerView.layoutManager = LinearLayoutManager(this)

        observeRealEstates()
        setupFab()
    }


    private fun observeRealEstates() {
        viewModel.allRealEstate.observe(this) {
            realestateRecyclerView.adapter = RealEstateAdapter(it)
        }
    }

    private fun setupFab(){
        val fab: View = binding.realestateAddFab

        fab.setOnClickListener { view ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ajouter immobilier")
            builder.setMessage("Vous allez ajouter un nouveau bien")
            builder.setPositiveButton("Confirmer") {
                    _, _ ->
                viewModel.addRealEstate(RealEstate("Maison 1", 100000, 100))
            }

            builder.setNegativeButton("Annuler") {
                    _, _ ->
                Toast.makeText(applicationContext, "Rien", Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }
    }


}