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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
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

       // setupNavigation()
        observeQuery()

        /*val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {*/
              /*  R.id.fragmentLoan -> //binding.listMapFab.visibility = View.GONE
                R.id.fragmentInput -> //binding.listMapFab.visibility = View.GONE
                R.id.fragmentMap -> observePane(R.id.fragmentMap)
                R.id.fragmentList -> observePane(R.id.fragmentList)*/



       /* binding.listMapFab.setOnClickListener {
            if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentList) {
                findNavController(R.id.navHostFragment).navigate(R.id.fragmentMap)
            } else if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentMap) {
                findNavController(R.id.navHostFragment).navigate(R.id.fragmentList)
            }
        }*/

        //observeFilteredList()

        //observeNetwork()
    }

    private fun observeNetwork() {
        viewModel.connectivityLiveData.observe(this) { networkAvailability ->
            if (networkAvailability == true) {
                Toast.makeText(this, "Device connected", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Network lost", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeQuery() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.noQuery.collect {
                    when (it) {
                        true -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Need at least one search field",
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
            if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentList) {
                findNavController(R.id.navHostFragment).navigate(R.id.action_fragmentList_to_filterDialogFragment)
            } else if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentMap) {
                findNavController(R.id.navHostFragment).navigate(R.id.action_fragmentMap_to_filterDialogFragment)
            }
            true
        }

        R.id.action_mortgage -> {
            if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentList) {
                findNavController(R.id.navHostFragment).navigate(R.id.fragmentLoan)
            } else if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentMap) {
                findNavController(R.id.navHostFragment).navigate(R.id.fragmentLoan)
            }
            true
        }

        R.id.action_add -> {
            findNavController(R.id.navHostFragment).navigate(R.id.fragmentInput)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setupNavigation(){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
       // return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

