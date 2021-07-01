package com.example.piraterun

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.piraterun.ui.treasure_hunt.TreasureHuntViewModel
import com.example.piraterun.ui.treasure_hunt.TreasureHuntViewModelFactory
import com.example.piraterun.util.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.piraterun.util.PermissionUtils.isPermissionGranted
import com.example.piraterun.util.PermissionUtils.requestPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.math.roundToInt


class TreasureHuntMapActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation : Location
    private var permissionDenied = false
    private var locationPermissionGranted = true
    private val defaultZoom = 15
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private val randomLocation = Location("")
    private var distance: Float = 10000f
    private var radius: Double = 0.0
    private var coins: Int = 0


    private lateinit var treasureHuntViewModel : TreasureHuntViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.treasure_hunt_map_activity)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        treasureHuntViewModel =
            TreasureHuntViewModelFactory((application as PirateApplication).userRepository).create(
                TreasureHuntViewModel::class.java
            )


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //get data from select screen
        radius = intent.getDoubleExtra("RADIUS", 1.5)
        radius *= 1000


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val textView = findViewById<TextView>(R.id.info_text)

        getDeviceLocation(textView)


            val submitButton = findViewById<Button>(R.id.submitButton)

            submitButton.setOnClickListener {
                getDeviceLocation(textView)
                if (distance != 10000f) {

                    if (distance > 50) {
                        Toast.makeText(this,"Ye're not quite there yet, pirate!",Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Well done, lad! You got $coins coins!", Toast.LENGTH_LONG).show()
                        treasureHuntViewModel.addCoins(coins)
                        startActivity(Intent(applicationContext, GameMenuActivity::class.java))
                    }
                }
            }

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.

     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()

    }

    private fun getDeviceLocation(textView: TextView) {

        /* Get the best and most recent location of the device, which may be null in rare
        * cases when a location is not available.*/

        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        if (task.result != null) {
                            // Set the map's camera position to the current location of the device.
                            currentLocation = task.result
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        currentLocation.latitude,
                                        currentLocation.longitude
                                    ), defaultZoom.toFloat()
                                )
                            )
                            if (treasureHuntViewModel.set == 0) {
                                getDataFromGoogleApi(currentLocation)
                                treasureHuntViewModel.set = 1
                            } else {
                                distance = randomLocation.distanceTo(currentLocation)
                            }
                            (String.format("%.2f", (distance / 1000)) + " km away").also { textView.text = it }

                        } else {

                            map.moveCamera(
                                CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, defaultZoom.toFloat())
                            )
                            map.uiSettings?.isMyLocationButtonEnabled = false
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDataFromGoogleApi(location: Location) {
        val types = arrayOf("church","art_gallery","museum","park","synagogue","university","tourist_attraction")
        var url = ""
        var nothingAround = false
        val thread = Thread {
            try {
                while(true) {
                    url =
                        URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.latitude + "," + location.longitude + "&radius=" + radius.toInt() + "&type=" + types[(0..6).random()] + "&key=AIzaSyApyov0lXmgmiA-NfuZsbmEBZB7JYYXOp0").readText()
                    val jsonObj = JSONObject(url)
                    println(jsonObj)
                    val sessionArray: JSONArray = jsonObj.getJSONArray("results")
                    if(sessionArray.length() == 0) {

                        nothingAround = true
                    }
                    val randomPlace = sessionArray.get((0 until sessionArray.length()).random())
                    val randomPlaceString = randomPlace.toString()
                    var latEndIndex = randomPlaceString.indexOf("\"lat\"") + 6
                    var lngEndIndex = randomPlaceString.indexOf("\"lng\"") + 6
                    //photos missing
                    var heightEndIndex = randomPlaceString.indexOf("\"height\"") + 9
                    var widthEndIndex = randomPlaceString.indexOf("\"width\"") + 8
                    var photoEndReferenceEndIndex =
                        randomPlaceString.indexOf("\"photo_reference\"") + 19

                    var latString = ""
                    var lngString = ""
                    var heightString = ""
                    var widthString = ""
                    var photoReferenceString = ""

                    while (latEndIndex < randomPlaceString.length && randomPlaceString[latEndIndex] != ',') {
                        latString += randomPlaceString[latEndIndex]
                        latEndIndex++
                    }
                    while (lngEndIndex < randomPlaceString.length && randomPlaceString[lngEndIndex] != '}') {
                        lngString += randomPlaceString[lngEndIndex]
                        lngEndIndex++
                    }

                    while (heightEndIndex < randomPlaceString.length && randomPlaceString[heightEndIndex] != ',') {
                        heightString += randomPlaceString[heightEndIndex]
                        heightEndIndex++
                    }

                    while (widthEndIndex < randomPlaceString.length && randomPlaceString[widthEndIndex] != '}') {
                        widthString += randomPlaceString[widthEndIndex]
                        widthEndIndex++
                    }

                    while (photoEndReferenceEndIndex < randomPlaceString.length && randomPlaceString[photoEndReferenceEndIndex] != '"') {
                        photoReferenceString += randomPlaceString[photoEndReferenceEndIndex]
                        photoEndReferenceEndIndex++
                    }

                    //makes sure there aren't any locations without a photo shown
                    if (!randomPlaceString.contains("photo")) {
                        continue
                    }


                    url =
                        "https://maps.googleapis.com/maps/api/place/photo?photoreference=$photoReferenceString&sensor=false&maxheight=$heightString&maxwidth=$widthString&key=AIzaSyApyov0lXmgmiA-NfuZsbmEBZB7JYYXOp0"


                    val randomLat = latString.toDouble()
                    val randomLng = lngString.toDouble()

                    randomLocation.latitude = randomLat
                    randomLocation.longitude = randomLng


                    distance = randomLocation.distanceTo(currentLocation)
                    coins = (distance/10).roundToInt()
                    break

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        thread.join()
        if(nothingAround) {
            Toast.makeText(this, "Nothing in the area!", Toast.LENGTH_LONG).show()
            startActivity(Intent(applicationContext, TreasureHuntActivity::class.java))
        }
        val imageView = findViewById<ImageView>(R.id.place_image)

        Picasso.with(this)
            .load(url)
            .into(imageView, object: com.squareup.picasso.Callback {
                override fun onSuccess() {
                    //set animations here

                }

                override fun onError() {

                }


            })


    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(
                this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
        // [END maps_check_location_permission]
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
            // [END_EXCLUDE]
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



}