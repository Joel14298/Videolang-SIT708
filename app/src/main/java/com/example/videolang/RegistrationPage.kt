package com.example.videolang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegistrationPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        val register = findViewById<Button>(R.id.register_button)
        register.setOnClickListener {
            Log.d("LoginPage", "Try to go to Login")

            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}