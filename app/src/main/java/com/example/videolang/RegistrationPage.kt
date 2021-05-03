package com.example.videolang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        val register = findViewById<Button>(R.id.register_button)
        register.setOnClickListener {
        registerMethod()
        }
    }

    private fun registerMethod(){
        Log.d("LoginPage", "Try to go to Login")

        val email = findViewById<TextInputEditText>(R.id.email_edittext_register).text.toString()
        val firstName = findViewById<TextInputEditText>(R.id.firstname_edittext_register).text.toString()
        val lastName = findViewById<TextInputEditText>(R.id.lastname_edittext_register).text.toString()
        val password = findViewById<TextInputEditText>(R.id.password_edittext_register).text.toString()
        val confirmPass = findViewById<TextInputEditText>(R.id.confirmpass_edittext_register).text.toString()

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || confirmPass.isEmpty()){

            Toast.makeText(this,"Missing field, Please Enter all the fields", Toast.LENGTH_LONG).show()
            return
        }
        else {
            if (password == confirmPass) {


                Log.d("RegistrationPage", "Email is: $email")
                Log.d("RegistrationPage", "First Name is: $firstName")
                Log.d("RegistrationPage", "Last Name is: $lastName")
                Log.d("RegistrationPage", "Password is: $password")
                Log.d("RegistrationPage", "Confirm Pass is: $confirmPass")

                // Registering user to Firebase
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                return@addOnCompleteListener
                            }
                            Log.d("RegistrationPage", "Successfully created user with uid: ${it.result?.user?.uid}")
                            saveUserToDb()

                        }
                        .addOnFailureListener{
                            Log.d("RegistrationPage", "Failed to register: ${it.message}")
                            Toast.makeText(this,"Please enter credentials in correct format", Toast.LENGTH_LONG).show()
                        }
                saveUserToDb()
            }
        }
    }

    private fun saveUserToDb(){
        val db = Firebase.database
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref= db.getReference("/users/$uid")

        val email = findViewById<TextInputEditText>(R.id.email_edittext_register).text.toString()
        val firstName = findViewById<TextInputEditText>(R.id.firstname_edittext_register).text.toString()
        val lastName = findViewById<TextInputEditText>(R.id.lastname_edittext_register).text.toString()


        val user = User(uid, firstName, lastName, email)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegistrationPage","Added to the DB")
                val intent = Intent(this, AllChats::class.java)
                startActivity(intent)
                Log.d("AllChats","Opening all chats page after registering user")
            }
    }
}

class User(val uid: String , val firstName: String, val lastName: String, val email: String)
