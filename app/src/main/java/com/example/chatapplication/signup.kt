package com.example.chatapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class signup : AppCompatActivity() {
    private lateinit var btnsignup: Button
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var auth: FirebaseAuth

    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        btnsignup = findViewById(R.id.btn_sendotp)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        countryCodePicker = findViewById(R.id.cpp)

        countryCodePicker.setOnCountryChangeListener {
            val countryCode = countryCodePicker.selectedCountryCode
            Toast.makeText(this, "Selected country code: $countryCode", Toast.LENGTH_SHORT).show()
        }

        btnsignup.setOnClickListener {
            val phoneNumber = editTextPhoneNumber.text.toString().trim()

            // Validate phone number
            if (phoneNumber.isNotEmpty() && phoneNumber.length >= 10) {  // Basic validation check
                val fullPhoneNumber = "+" + countryCodePicker.selectedCountryCode + phoneNumber
                startPhoneNumberVerification(fullPhoneNumber)
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@signup, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            resendToken = token

            // Navigate to OTP activity
            val intent = Intent(this@signup, OTP::class.java)
            intent.putExtra("verificationId", verificationId)
            startActivity(intent)
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->             
                if (task.isSuccessful) {
                    // Sign in success
                    val user = task.result?.user
                    Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
                    // Navigate to main activity or next step
                } else {
                    // Sign in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid code entered", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
