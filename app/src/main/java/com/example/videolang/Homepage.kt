package com.example.videolang

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)


        bottomNavigationView()



    }

    private fun bottomNavigationView(){
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.page_2 -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.page_3 -> {
                    // Respond to navigation item 2 click
                    true

                }
                R.id.page_4 -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }

    }
}