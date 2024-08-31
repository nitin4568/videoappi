package com.example.chatapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.OutputStream
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class profilesetup : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val GALLERY_REQUEST_CODE = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilesetup)
        val cameraIcon = findViewById<ImageView>(R.id.img)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cameraIcon.setOnClickListener {
            checkCameraPermission()
        }
        val btnNext = findViewById<Button>(R.id.btnnext)
        btnNext.setOnClickListener {
            val intent = Intent(this, UserchatActivity::class.java)
            startActivity(intent)
        }
        val myVariable: Int = 10
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(
                this,
                "No camera app found on this device.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun saveImageToStorage(bitmap: Bitmap) {
        val filename = "profile_image.jpg"
        val outputStream: OutputStream
        try {
            outputStream = openFileOutput(filename, MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            Toast.makeText(
                this,
                "Image saved to $filesDir/$filename",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Failed to save image.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to open the camera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Display the captured image in the ImageView
            val imageView = findViewById<ImageView>(R.id.img)
            imageView.setImageBitmap(imageBitmap)

            // Save the image to local storage if needed
            saveImageToStorage(imageBitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Handle camera capture result

        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Handle gallery selection result
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                try {
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                    val imageView = findViewById<ImageView>(R.id.img)
                    imageView.setImageBitmap(imageBitmap)
                    // Save the image to local storage if needed
                    saveImageToStorage(imageBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Failed to load selected image.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}



