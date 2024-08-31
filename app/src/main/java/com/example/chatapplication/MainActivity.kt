package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import com.google.firebase.FirebaseApp
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.chatapplication.adapter.ViewPagerAdapter
import com.example.chatapplication.databinding.ActivityUserchatBinding
import com.example.chatapplication.ui.CallsFragment
import com.example.chatapplication.ui.ChatsFragment
import com.example.chatapplication.ui.StatusFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseApp.initializeApp(this)

    }
}