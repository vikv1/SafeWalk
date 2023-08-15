package com.example.mapthing
/*
TODO: LIGHT MODE DARK MODE SWITCH
TODO: GET GOOD LOOKING MARKER PINS
TODO: SEARCH FOR PLACES/ADDRESSES
TODO: CUSTOM ROUTE USING OSM DATA
TODO: MAKE APP LOOK COOL
 */

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.camera.CameraTrackingMode
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.style.StyleMode
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.search.Search
import com.tomtom.sdk.search.ui.SearchFragment
import com.tomtom.sdk.search.ui.model.SearchApiParameters
import com.tomtom.sdk.search.ui.model.SearchProperties
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


class MainActivity : AppCompatActivity() {
    private lateinit var gpsHelper: GPSHelper
    private val mapOptions = MapOptions(mapKey = "eAj94NPqWfdj6tJ61henOfbye1t387le")
    private val mapFrag = MapFragment.newInstance(mapOptions)

    private val androidLocationProviderConfig = AndroidLocationProviderConfig(
        minTimeInterval = 250L.milliseconds,
        minDistance = Distance.meters(20.0)
    )

    private lateinit var locationProvider: LocationProvider

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        locationProvider = AndroidLocationProvider(
            context = applicationContext,
            config = androidLocationProviderConfig
        )

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

        val searchApiParameters = SearchApiParameters(
            limit = 3,
            position = GeoPoint(currentLat, currentLng)
        )

        val searchProperties = SearchProperties(
            searchApiKey = "eAj94NPqWfdj6tJ61henOfbye1t387le",
            searchApiParameters = searchApiParameters,
            commands = listOf("TomTom")
        )

        val searchFragment = SearchFragment.newInstance(searchProperties)
        supportFragmentManager.beginTransaction()
            .replace(R.id.search_fragment_container, searchFragment)
            .commitNow()

//        val search: Search
//        searchFragment.setSearchApi(search)


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
            // marks user location on map
            val locationMarkerOptions = LocationMarkerOptions(
                type = LocationMarkerOptions.Type.Chevron,
                markerMagnification = 0.5,
            )
            tomTomMap.setLocationProvider(locationProvider)
            locationProvider.enable()
            tomTomMap.enableLocationMarker(locationMarkerOptions)

            // tells camera to follow user marker
            // this also means user cannot move camera besides zoom while active
            tomTomMap.cameraTrackingMode = CameraTrackingMode.Follow

            val newCameraOptions = CameraOptions(
                position = GeoPoint(lat, long),
                zoom = 11.0,
                tilt = 0.0,
                rotation = 0.0,
            )

            tomTomMap.animateCamera(newCameraOptions, 6.seconds)



            val lightToggle = findViewById<FloatingActionButton>(R.id.light_toggle)
            val darkToggle = findViewById<FloatingActionButton>(R.id.dark_toggle)

            lightToggle.setOnClickListener {
                tomTomMap.setStyleMode(StyleMode.MAIN)
            }

            darkToggle.setOnClickListener {
                tomTomMap.setStyleMode(StyleMode.DARK)
            }

        }
    }


}
