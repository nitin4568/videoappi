package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class Password : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore

    private lateinit var btnSave: Button
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set up views
        btnSave = findViewById(R.id.btn_save)
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_num) // Make sure this ID matches your layout file
        edtPassword = findViewById(R.id.edt_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email)) {
                    saveUserToFirestore(name, email, password)
                } else {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToFirestore(name: String, email: String, password: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "password" to password // It's generally not a good practice to save passwords in plaintext
        )

        firestore.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
                // Navigate to profile setup or main activity
                val intent = Intent(this, profilesetup::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }
}
