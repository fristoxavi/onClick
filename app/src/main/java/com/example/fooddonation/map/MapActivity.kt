package com.example.fooddonation.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fooddonation.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment


class MapActivity : AppCompatActivity() {

    private var googleMap: GoogleMap? = null
    lateinit var fusedLocationClient:FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        checkPermissions()
    }

    private fun checkPermissions() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        } else {
            // Permission is already granted, get the current location
            getCurrentLocationAndLaunchMap()
        }
    }

    private fun getCurrentLocationAndLaunchMap() {
        // Check if location permission is granted again (for safety)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // Use the obtained location to launch Google Maps
                location?.let {
                    val place = intent.getStringExtra("place")
                    val destination = "Aluva" // Replace with your actual destination location

                    val uri =
                        Uri.parse("https://www.google.com/maps/dir/${it.latitude},${it.longitude}/$place")
                    val intent = Intent(Intent.ACTION_VIEW, uri)

                    // Optional: Set the package name to launch Google Maps directly
                    intent.setPackage("com.google.android.apps.maps")

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        } else {
            // Handle case where permission is not granted
            // You may display a message to the user or take other appropriate action
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the current location
                getCurrentLocationAndLaunchMap()
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
    }

}




