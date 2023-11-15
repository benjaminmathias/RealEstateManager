package com.openclassrooms.realestatemanager.ui

import android.content.res.Configuration
import android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.AbstractListDetailFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.model.data.RealEstate
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RealEstateListFragment : AbstractListDetailFragment() {

    private lateinit var realestateRecyclerView: RecyclerView
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: RealEstateAdapter

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateListPaneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreateDetailPaneNavHostFragment(): NavHostFragment {
        return NavHostFragment.create(R.navigation.two_pane_navigation)
    }

    override fun onListPaneViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onListPaneViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            TwoPaneOnBackPressedCallback(slidingPaneLayout)
        )

        binding.listFab?.setOnClickListener {
            findNavController().navigate(R.id.fragmentMap)
        }

        realestateRecyclerView = binding.realestateListRecyclerView
        adapter = RealEstateAdapter(mutableListOf())

        adapter.setOnClickListener(object :
            RealEstateAdapter.OnClickListener {
            override fun onClick(position: Int, model: RealEstate) {
                openDetails(model.id!!)
            }
        })

        realestateRecyclerView.adapter = adapter

        collectState()
        observeFilteredList()
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is MainViewModel.MainUiState.Success -> {
                            adapter.updateData(uiState.realEstateList)
                            binding.emptyView.root.visibility = View.GONE

                            collectFilteredValue()

                            if (!realestateRecyclerView.isComputingLayout) {
                                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                                    resources.configuration.isLayoutSizeAtLeast(
                                        SCREENLAYOUT_SIZE_LARGE
                                    )
                                ) {
                                    if (uiState.realEstateList.isNotEmpty()) {
                                        uiState.realEstateList[0].id?.let { openDetails(it) }
                                    } else {
                                        openDetails(null)
                                    }
                                }
                            }
                        }

                        is MainViewModel.MainUiState.Error -> {
                            binding.emptyView.root.visibility = View.VISIBLE
                            binding.emptyView.title.text = "There was an error getting the list"
                            binding.emptyView.subTitle.text = uiState.exception.message
                        }
                    }
                }
            }
        }
    }

    private fun collectFilteredValue() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFiltered.collect {
                    when (it) {
                        true -> {
                            if (realestateRecyclerView.adapter?.itemCount == 0) {
                                binding.emptyView.root.visibility = View.VISIBLE
                                binding.emptyView.title.text =
                                    "There's no data available !"
                                binding.emptyView.subTitle.text =
                                    "There's no result matching your query"
                            }
                        }

                        false -> {
                            if (realestateRecyclerView.adapter?.itemCount == 0) {
                                binding.emptyView.root.visibility = View.VISIBLE
                                binding.emptyView.title.text =
                                    "There's no data available !"
                                binding.emptyView.subTitle.text =
                                    "You need to add a new property for it to be shown !"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeFilteredList() {
        lifecycleScope.launch {
            viewModel.isFiltered.collect {
                if (it) {
                    if (realestateRecyclerView.adapter?.itemCount == 0) {
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                            resources.configuration.isLayoutSizeAtLeast(
                                SCREENLAYOUT_SIZE_LARGE
                            )
                        ) {
                            openDetails(null)
                        }
                    }
                    binding.realestateListFilterClear?.visibility = View.VISIBLE

                    binding.realestateListFilterClear?.setOnClickListener {
                        viewModel.getNonFilteredList()
                    }
                } else {
                    binding.realestateListFilterClear?.visibility = View.GONE
                }
            }
        }
    }

    private fun openDetails(itemId: Long?) {
        val bundle = Bundle().apply {
            if (itemId != null) {
                putLong("id", itemId)
            }
        }
        val detailNavController = detailPaneNavHostFragment.navController
        detailNavController.navigate(
            R.id.fragmentDetails,
            bundle,
            NavOptions.Builder()
                .setPopUpTo(detailNavController.graph.startDestinationId, true)
                .apply {
                    if (slidingPaneLayout.isOpen) {
                        setEnterAnim(R.animator.nav_default_enter_anim)
                        setExitAnim(R.animator.nav_default_exit_anim)
                    }
                }
                .build()
        )
        slidingPaneLayout.open()
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
    }
}

