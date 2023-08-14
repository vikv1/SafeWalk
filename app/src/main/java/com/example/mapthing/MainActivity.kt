package com.example.mapthing
/*
TODO: LIGHT MODE DARK MODE SWITCH
TODO: GET GOOD LOOKING MARKER PINS
TODO: SEARCH FOR PLACES/ADDRESSES
TODO: CUSTOM ROUTE USING OSM DATA
TODO: MAKE APP LOOK COOL
 */

import android.app.PendingIntent.getActivity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.style.StyleMode
import com.tomtom.sdk.map.display.ui.MapFragment
import kotlin.time.Duration.Companion.seconds


class MainActivity : AppCompatActivity() {
    private lateinit var gpsHelper: GPSHelper
    private val mapOptions = MapOptions(mapKey = "eAj94NPqWfdj6tJ61henOfbye1t387le")
    private val mapFrag = MapFragment.newInstance(mapOptions)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)){
                val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this,
                    permissions, 1);
            }else{
                val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this,
                    permissions, 1);
            }
        }

        gpsHelper = GPSHelper(this)

        val currentLatLng = gpsHelper.myLocation
        var currentLat = currentLatLng.latitude
        var currentLng = currentLatLng.longitude


        initMap(currentLat, currentLng)
    }


    @Override
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
    grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
             1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    private fun initMap(lat: Double, long: Double) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFrag)
            .commit()


        mapFrag.getMapAsync { tomTomMap: TomTomMap ->
            tomTomMap.setStyleMode(StyleMode.DARK)
            val markerOptions = MarkerOptions(
                coordinate = GeoPoint(lat, long),
                pinImage = ImageFactory.fromResource(R.drawable.ic_launcher_foreground)
            )

            tomTomMap.addMarker(markerOptions)

            tomTomMap.moveCamera(
                CameraOptions(
                    position = GeoPoint(0.0, 0.0),
                    zoom = 0.0,
                    tilt = 0.0,
                    rotation = 25.0,
                )
            )

            val newCameraOptions = CameraOptions(
                position = GeoPoint(lat, long),
                zoom = 9.0,
                tilt = 25.0,
                rotation = 0.0,
            )

            if(lat != 0.0 || long != 0.0) {
                tomTomMap.animateCamera(newCameraOptions, 3.seconds)
            }

        }
    }

}
