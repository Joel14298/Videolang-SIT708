package com.example.videolang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class LoginPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)


    val login = findViewById<Button>(R.id.signInButton)
        login.setOnClickListener {
            val email= findViewById<TextInputEditText>(R.id.email_edittext_login).text.toString()
            val password = findViewById<TextInputEditText>(R.id.password_edittext_login).text.toString()


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Missing field, Please Enter all the fields", Toast.LENGTH_LONG).show()
            } else {

                Log.d("LoginPage","Attempt to login:$email/***")

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                return@addOnCompleteListener
                            } else {
                                Log.d("LoginPage", "Logged in with user: $email")
                                val intent = Intent(this, Homepage::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener{
                            Log.d("LoginPage", "Failed to login: ${it.message}")
                            Toast.makeText(this,"Login Failure", Toast.LENGTH_LONG).show()
                        }
            }

        }

    }

}