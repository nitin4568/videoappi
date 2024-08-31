package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Apply window insets to adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Start the next activity after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,login::class.java))
            finish()
        }, 2000) // 1200 milliseconds delay
    }

    private fun enableEdgeToEdge() {
        // You can implement this method if needed for edge-to-edge display handling
        // Example: window.insetsController?.show(WindowInsets.Type.systemBars())
    }
}
