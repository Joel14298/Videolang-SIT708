package com.example.videolang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getStart = findViewById<Button>(R.id.getStartedButton)
        getStart.setOnClickListener {
            Log.d("MainActivity","Try to go to register")

            val intent = Intent(this,RegistrationPage::class.java)
            startActivity(intent)
        }
    }
}