package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OTP : AppCompatActivity() {
    private lateinit var btnVerifyOTP: Button
    private lateinit var editTextOTP: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        auth = FirebaseAuth.getInstance()

        btnVerifyOTP = findViewById(R.id.btn_verify)
        editTextOTP = findViewById(R.id.edt_otp)

        val verificationId = intent.getStringExtra("verificationId")

        btnVerifyOTP.setOnClickListener {
            val otp = editTextOTP.text.toString().trim()
            if (otp.isNotEmpty() && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = task.result?.user
                    // Proceed to the next activity
                    Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Password::class.java)
                    startActivity(intent)
                } else {
                    // Sign in failed
                    Toast.makeText(this, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
