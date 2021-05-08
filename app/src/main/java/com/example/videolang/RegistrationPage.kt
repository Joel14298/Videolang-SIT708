package com.example.videolang

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.parcelize.Parcelize
import java.util.*

class RegistrationPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        val selectPhoto = findViewById<Button>(R.id.select_profile_picture)

        selectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
           startActivityForResult(intent,0)
        }

        val loginRegister = findViewById<TextView>(R.id.login_button_textview)

        loginRegister.setOnClickListener{
            val intent = Intent(this, HomepageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val register = findViewById<Button>(R.id.register_button)
        register.setOnClickListener {
        registerMethod()
        }
    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val selectPhoto = findViewById<Button>(R.id.select_profile_picture)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            val selectPhotoImageView = findViewById<CircleImageView>(R.id.circle_imageview_register)
            selectPhotoImageView.setImageBitmap(bitmap)
        }
    }
    private fun registerMethod(){
        Log.d("RegistrationPage", "Try to go to Login")

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

//                 Registering user to Firebase
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                return@addOnCompleteListener
                            }
                            Log.d("RegistrationPage", "Successfully created user with uid: ${it.result?.user?.uid}")
                            uploadImageToFireBBase()
                        }
                        .addOnFailureListener{
                            Log.d("RegistrationPage", "Failed to register: ${it.message}")
                            Toast.makeText(this,"Please enter credentials in correct format", Toast.LENGTH_LONG).show()
                        }

            }
        }
    }

    private fun uploadImageToFireBBase(){
        if(selectedPhotoUri == null)return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegistrationPage","Successfully added image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegistrationPage","Successful downloaded image: $it ")
                    saveUserToDb(it.toString())
                }
                    .addOnFailureListener {
                        Log.d("RegistrationPage","error while loading data onto the storage")
                    }
            }
    }
    private fun saveUserToDb(profilePictureUri: String){
        val db = Firebase.database
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref= db.getReference("/users/$uid")

        val email = findViewById<TextInputEditText>(R.id.email_edittext_register).text.toString()
        val firstName = findViewById<TextInputEditText>(R.id.firstname_edittext_register).text.toString()
        val lastName = findViewById<TextInputEditText>(R.id.lastname_edittext_register).text.toString()


        val user = User(uid, firstName, lastName, email, profilePictureUri)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegistrationPage","Added to the DB")
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                Log.d("RegistrationPage","Opening all chats page after registering user")
            }
            .addOnFailureListener {
                Log.d("RegistrationPage","Error while loading data into DB")
            }
    }
}

@Parcelize
class User(val uid: String , val firstName: String, val lastName: String, val email: String, var profilePictureUri: String):Parcelable {
    constructor(): this("","","","","")
}
