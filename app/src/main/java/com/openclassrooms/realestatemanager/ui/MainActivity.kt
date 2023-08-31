package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNav: BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        bottomNav = binding.bottomNavigation

        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.fragmentDetails ||
                destination.id == R.id.fragmentInput
            ) {
                bottomNav.visibility = View.GONE
                binding.realestateAddFab.visibility = View.GONE
            } else {
                bottomNav.visibility = View.VISIBLE
                binding.realestateAddFab.visibility = View.VISIBLE
            }
        }

        binding.realestateAddFab.setOnClickListener {
            findNavController(R.id.navHostFragment).navigate(R.id.fragmentInput)
        }

        requestLocationPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_filter -> {
            Toast.makeText(this, "Click on Filter button", Toast.LENGTH_LONG).show()

            if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentList) {
                findNavController(R.id.navHostFragment).navigate(R.id.action_fragmentList_to_filterDialogFragment)
            } else if (findNavController(R.id.navHostFragment).currentDestination?.id == R.id.fragmentMap) {
                findNavController(R.id.navHostFragment).navigate(R.id.action_fragmentMap_to_filterDialogFragment)
            }

            true
        }

        R.id.action_mortgage -> {
            Toast.makeText(this, "Click on Mortgage button", Toast.LENGTH_LONG).show()
            true
        }

        R.id.action_settings -> {
            Toast.makeText(this, "Click on Settings button", Toast.LENGTH_LONG).show()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                Toast.makeText(this, "Permission granted !", Toast.LENGTH_LONG).show()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                Toast.makeText(this, "Permission granted !", Toast.LENGTH_LONG).show()
            }

            else -> {
                // No location access granted.
                Toast.makeText(this, "Permission denied !", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

