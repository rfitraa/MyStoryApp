package com.dicoding.mystoryapp.ui

import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityMapsBinding
import com.dicoding.mystoryapp.viewmodel.MapsViewModel
import com.dicoding.mystoryapp.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val mapsViewModel: MapsViewModel by viewModels { viewModelFactory }
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModelFactory = ViewModelFactory.getInstance(binding.root.context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setMapStyle()
        getMyLocation()
        getStoryLocation()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
            isGranted : Boolean ->
        if (isGranted){
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val myLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12f))
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }else{
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun getStoryLocation(){
        mapsViewModel. getMapStory().observe(this){ it ->
            if (it != null){
                if (it is com.dicoding.mystoryapp.data.Result.Error) {
                    Toast.makeText(this, "There is an error", Toast.LENGTH_SHORT).show()
                }
                else if (it is com.dicoding.mystoryapp.data.Result.Success) {
                    it.data.listStory.forEach{
                        val latLng = LatLng(it.lat, it.lon)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(it.name)
                                .snippet("Lat : ${it.lat} , Lon : ${it.lon}")
                        )
                        boundsBuilder.include(latLng)
                    }
                }
            }
        }
    }

    companion object{
        const val TAG = "MapsActivity"
    }
}