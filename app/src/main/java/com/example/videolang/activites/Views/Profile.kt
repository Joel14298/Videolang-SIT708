package com.example.videolang.activites.Views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videolang.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val bottomnavaigation = findViewById<BottomNavigationView>(R.id.bottomBar)
        bottomnavaigation.menu.getItem(4).isChecked = true;
        bottomnavaigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.item0 -> {
                    val intent = Intent(this, Notification::class.java)

                    startActivity(intent)
                }

                R.id.item1 -> {
                    val intent = Intent(this, Calendar::class.java)

                    startActivity(intent)
                }
                R.id.item2 -> {
                    val intent = Intent(this, Calls::class.java)
                    startActivity(intent)
                }
                R.id.item3 -> {
//                    val intent = Intent(this, Profile::class.java)
//                    startActivity(intent)
                }
                R.id.item4 -> {
                    val intent = Intent(this, HomepageActivity::class.java)
                    startActivity(intent)
                }

            }
            true
        }
    }
}