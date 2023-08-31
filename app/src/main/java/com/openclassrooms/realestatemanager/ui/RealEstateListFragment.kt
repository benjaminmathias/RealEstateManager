package com.openclassrooms.realestatemanager.ui

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
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
import java.util.Locale

@AndroidEntryPoint
class RealEstateListFragment : Fragment() {

    private lateinit var realestateRecyclerView: RecyclerView
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: RealEstateAdapter

    private val viewModel: MainViewModel by viewModels()

    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)

        activity?.title = "Real Estate Manager"
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun setupRecyclerView() {
        realestateRecyclerView = binding.realestateListFragment
        realestateRecyclerView.layoutManager = LinearLayoutManager(context)
        realestateRecyclerView.addItemDecoration(
            DividerItemDecoration(
                realestateRecyclerView.context,
                DividerItemDecoration.HORIZONTAL
            )
        )
        adapter = RealEstateAdapter(this@RealEstateListFragment, mutableListOf())
        realestateRecyclerView.adapter = adapter
    }

    private fun observeData() {

        /* viewModel.uiStateLiveData.observe(viewLifecycleOwner) { uiState ->

            when (uiState) {

                is MainViewModel.MainUiState.Success -> {
                    adapter.updateData(uiState.realEstateEntity)
                }

                is MainViewModel.MainUiState.Error -> RealEstateAdapter(
                    this@RealEstateListFragment,
                    mutableListOf()
                )

                else -> {}
            }
        }*/

        /*lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        is MainViewModel.MainUiState.Success -> {
                            adapter.updateData(uiState.realEstateEntity)
                        }

                        is MainViewModel.MainUiState.Filter -> {
                            adapter.updateData(uiState.realEstateFilteredList)
                        }

                        is MainViewModel.MainUiState.Error -> RealEstateAdapter(
                            this@RealEstateListFragment,
                            mutableListOf()
                        )

                        else -> {}
                    }
                }*/

        // TODO : check ici pour update recyclerview
         viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is MainViewModel.MainUiState.Success -> {
                            adapter.updateData(uiState.realEstateEntity)
                        }

                        is MainViewModel.MainUiState.Error -> RealEstateAdapter(
                            this@RealEstateListFragment,
                            mutableListOf()
                        )

                        else -> {}
                    }
                }
            }
        }
    }

    fun onClick(id: Long) {
        Toast.makeText(context, "Click on $id", Toast.LENGTH_SHORT).show()
        val bundle = Bundle().apply {
            putLong("id", id)
        }
        requireView().findNavController().navigate(R.id.fragmentDetails, bundle)
    }


    override fun onStart() {
        super.onStart()
        Log.d("RealEstateListFragment", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("RealEstateListFragment", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("RealEstateListFragment", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("RealEstateListFragment", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RealEstateListFragment", "onDestroy called")
    }


}