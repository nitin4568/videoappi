package com.example.chatapplication

import android.os.Bundle
import android.view.Menu
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.chatapplication.adapter.ViewPagerAdapter
import com.example.chatapplication.databinding.ActivityUserchatBinding
import com.example.chatapplication.ui.CallsFragment
import com.example.chatapplication.ui.ChatsFragment
import com.example.chatapplication.ui.StatusFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class UserchatActivity : AppCompatActivity() {
    private lateinit var floatingActionButton: ExtendedFloatingActionButton
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val CAMERA_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val GALLERY_REQUEST_CODE = 200
    private  var binding: ActivityUserchatBinding ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserchatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.appBarUserchat.toolbar)
        val fragmentArrayList = ArrayList<Fragment>()
        fragmentArrayList.add(CallsFragment())
        fragmentArrayList.add(StatusFragment())
        fragmentArrayList.add(ChatsFragment())
        val adapter = ViewPagerAdapter(this, supportFragmentManager, fragmentArrayList)
        binding!!.appBarUserchat.viewPager.adapter=adapter
        binding!!.appBarUserchat.tabs.setupWithViewPager(binding!!.appBarUserchat.viewPager)
        binding!!.appBarUserchat.fab.setOnClickListener { view ->
            Snackbar.make(
                view,
                "Sorry, I can't find your contact. Please contact Nitin.",
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding!!.drawerLayout
        val navView: NavigationView = binding!!.navView
        val navController = findNavController(R.id.nav_host_fragment_content_userchat)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.userchat, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_userchat)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

