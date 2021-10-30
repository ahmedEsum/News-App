package com.example.android.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.newsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.breakingNewsFragment2,
                R.id.savedNewsFragment2,
                R.id.searchNewsFragment2
            )
        )
        setupActionBarWithNavController(
            findNavController(R.id.newsNavHostFragment),
            appBarConfiguration
        )
        bottomNavigation.setupWithNavController(findNavController(R.id.newsNavHostFragment))
        bottomNavigation.setOnItemSelectedListener {
            if(it.itemId != bottomNavigation.selectedItemId) {
                NavigationUI.onNavDestinationSelected(it,findNavController(R.id.newsNavHostFragment))
            }
            true
        }

    }
}