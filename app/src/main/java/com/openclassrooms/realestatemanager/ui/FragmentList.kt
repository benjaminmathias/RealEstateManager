package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FragmentList : Fragment() {

    private lateinit var realestateRecyclerView: RecyclerView
    private lateinit var binding: FragmentListBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)

        activity?.title = "RealEstateManager"

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        realestateRecyclerView = binding.realestateListFragment
        realestateRecyclerView.layoutManager = LinearLayoutManager(context)
        realestateRecyclerView.addItemDecoration(DividerItemDecoration(realestateRecyclerView.context, DividerItemDecoration.HORIZONTAL))

        val adapter = RealEstateAdapter(this@FragmentList, emptyList())
        realestateRecyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is MainViewModel.MainUiState.Success -> adapter.updateData(uiState.realEstateEntity)
                        is MainViewModel.MainUiState.Error -> RealEstateAdapter(this@FragmentList, emptyList())
                    }

                }
            }
        }
    }

    fun onClick(id: Long){
        Toast.makeText(context, "Click on $id", Toast.LENGTH_SHORT).show()

        //val fragmentTransaction = parentFragmentManager.beginTransaction()
        val bundle = Bundle().apply {
            putLong("id", id)
        }
      //  val fragmentDetails = FragmentDetails()
      //  fragmentDetails.arguments = bundle

        requireView().findNavController().navigate(R.id.fragmentDetails, bundle)
       // fragmentTransaction.replace(R.id.navHostFragment, fragmentDetails)
       // fragmentTransaction.commit()
    }
}