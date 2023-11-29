package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController = navHostFragment.findNavController()

        observeQuery()
    }

    private fun observeQuery() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.noQuery.collect {
                    when (it) {
                        true -> {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.filter_error_message),
                                Toast.LENGTH_SHORT
                            ).show()

                            viewModel.setNoQueryToFalse()
                        }

                        false -> {}
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_filter -> {
            if (navController.currentDestination?.id == R.id.fragmentList) {
                navController.navigate(R.id.action_fragmentList_to_filterDialogFragment)
            } else if (navController.currentDestination?.id == R.id.fragmentMap) {
                navController.navigate(R.id.action_fragmentMap_to_filterDialogFragment)
            }
            true
        }

        R.id.action_mortgage -> {
            if (navController.currentDestination?.id == R.id.fragmentList) {
                navController.navigate(R.id.fragmentLoan)
            } else if (navController.currentDestination?.id == R.id.fragmentMap) {
                navController.navigate(R.id.fragmentLoan)
            }
            true
        }

        R.id.action_add -> {
            navController.navigate(R.id.fragmentInput)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

