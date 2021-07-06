package com.example.piraterun

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.piraterun.databinding.*
import com.example.piraterun.ui.login.LoginViewModel
import com.example.piraterun.ui.login.LoginViewModelFactory
import com.example.piraterun.ui.login.ScavengeViewModelFactory
import com.example.piraterun.ui.scavenge.ScavengeGameOverFragment
import com.example.piraterun.ui.scavenge.ScavengeSelectFragment
import com.example.piraterun.ui.scavenge.ScavengeShopFragment
import com.example.piraterun.ui.scavenge.ScavengeViewModel
import com.example.piraterun.util.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.piraterun.util.PermissionUtils.isPermissionGranted
import com.example.piraterun.util.PermissionUtils.requestPermission
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


class ScavengeMapActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {

    // general variables
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationPermissionGranted = true
    private var permissionDenied = false
    private var defaultZoom = 15.0
    private val shopFragment: Fragment = ScavengeShopFragment()
    private lateinit var selectBinding: ScavengeLocationSelectBinding
    private lateinit var binding: ScavengeGameFragmentBinding
    private lateinit var shopBinding: ScavengeShopFragmentBinding
    private lateinit var gameOverBinding: ScavengeGameOverBinding
    var lobbyOwner = false
    private var locationsInitialized = false

    // session variables
    private lateinit var gameRef: DocumentReference
    lateinit var gameSessionId: String
    private lateinit var currentLocation: LatLng

    private val allMarkers = mutableListOf<Marker>()
    private val myChests = mutableListOf<LatLng>()
    private var enemyChests = mutableListOf<LatLng>()


    // gameplay variables
    private var gameReady = false
    private var enemyReady = false
    private var gameStarted = false
    private var gameOver = false


    private var foundLocations = mutableListOf<LatLng>()
    private var foundChests = 0
    private var shopOpen = false
    private var chestNumber = 0
    private var coinNumber = 0
    private var playerCoins = 100
    private var radius = 0.0
    private lateinit var viewModel: ScavengeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scavenge_map_activity)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel =
            ScavengeViewModelFactory((application as PirateApplication).userRepository).create(
                ScavengeViewModel::class.java
            )

        // initialization
        binding = ScavengeGameFragmentBinding.inflate(layoutInflater)
        selectBinding = ScavengeLocationSelectBinding.inflate(layoutInflater)
        shopBinding = ScavengeShopFragmentBinding.inflate(layoutInflater)
        gameOverBinding = ScavengeGameOverBinding.inflate(layoutInflater)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this) // Location Client for getting current location

        // set select screen on start

      /*  addContentView(selectBinding.root,
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ))*/


        val newFragment: Fragment = ScavengeSelectFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, newFragment).commit()

        val mapFragment =
            supportFragmentManager        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // set gameplay variables

        lobbyOwner = intent.getBooleanExtra("LOBBY_OWNER", false)
        radius = intent.getDoubleExtra("RADIUS", 5.0)
        defaultZoom = when {
            radius > 6 -> {
                12.0
            }
            radius > 3 -> {
                13.0
            }
            radius > 1 -> {
                14.0
            }
            else -> {
                15.0
            }
        }
        gameSessionId = intent.getStringExtra("GAME_SESSION_ID").toString()

        gameRef =
            FirebaseFirestore.getInstance().collection("game_sessions").document(gameSessionId)
        chestNumber = intent.getIntExtra("CHEST_NUMBER", 5)
        coinNumber = intent.getIntExtra("COIN_NUMBER", 5)

        viewModel.remainingChests.observe(this) {
            findViewById<TextView>(R.id.remainingChests).text = it.toString()
        }
        viewModel.setChestNumber(chestNumber)
    }

    private fun updateGameVariables() {
        gameRef.get().addOnSuccessListener {
            chestNumber = it.getLong("chest_goal")!!.toInt()

            if (gameReady) {
                if (lobbyOwner) {
                    val locations = it.get("player2_locations")
                    if (locations != null) {
                        convertLocations(enemyChests, locations as ArrayList<*>)
                    }

                } else {
                    val locations = it.get("player1_locations")
                    if (locations != null) {
                        convertLocations(enemyChests, locations as ArrayList<*>)
                    }
                }
            }

            enemyReady = if (lobbyOwner) {
                it.getBoolean("player2_ready")!!
            } else {
                it.getBoolean("player1_ready")!!
            }
        }
    }

    private fun convertLocations(destinationContainer: MutableList<LatLng>, list: ArrayList<*>) {
        for (i in 0 until list.size) {
            val element = list[i] as HashMap<*, *>
            val latitude = element["latitude"] as Double
            val longitude = element["longitude"] as Double
            destinationContainer.add(LatLng(latitude, longitude))
        }
    }

    // this event will enable the back
    // function to the button on press
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.setOnMarkerClickListener(this)

        enableMyLocation()

        gameRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("yeet", "Listen failed.", e)
                return@addSnapshotListener
            }
            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot.exists()) {
                /** Game session updates are processed here **/
                Log.d("yeet", "$source data: ${snapshot.data}")
                updateGameVariables()
                if (!lobbyOwner && !locationsInitialized) {

                    var positions = snapshot.get("locations") as ArrayList<*>?
                    Log.i("locations", positions.toString())
                    if (positions != null) {
                        locationsInitialized = true
                        Log.i("locations", positions.toString())
                        val convertedPositions = mutableListOf<LatLng>()

                        for (i in 0 until positions.size) {
                            val element = positions[i] as HashMap<*, *>
                            val latitude = element["latitude"] as Double
                            val longitude = element["longitude"] as Double
                            convertedPositions.add(LatLng(latitude, longitude))
                        }
                        setAllMarkers(convertedPositions)
                    }
                }

                val player1Won = snapshot.getBoolean("player1_winner")
                val player2Won = snapshot.getBoolean("player2_winner")

                if (player1Won!! && !lobbyOwner) {
                    finishGame(false)
                } else if (player2Won!! && lobbyOwner) {
                    finishGame(false)
                }

            } else {
                Log.d("yeet", "$source data: null")
            }
        }

        prepareGame()
    }

    fun toggleShop() {
        // TODO: add shop functionality
        if (!shopOpen) {

            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, shopFragment).addToBackStack(null).commit()
            shopOpen = true
        } else {
            supportFragmentManager.popBackStack()
            shopOpen = false
        }
    }

    private fun prepareGame() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.

                        if (task != null) {
                            currentLocation = LatLng(task.result.latitude, task.result.longitude)
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        currentLocation.latitude,
                                        currentLocation.longitude
                                    ), defaultZoom.toFloat()
                                )
                            )


                            // TODO: implementation for different player locations
                            /** generate random locations if lobby owner or retrieve them from firestore if not lobby owner **/

                            if (lobbyOwner) {
                                val allLocations = mutableListOf<LatLng>()
                                val realRadius = (radius / (111 * 2))
                                val random = Random
                                val currentLatitude = currentLocation.latitude
                                val currentLongitude = currentLocation.longitude

                                for (i in 0 until chestNumber * 2) {
                                    allLocations.add(
                                        LatLng(
                                            random.nextDouble(
                                                -realRadius,
                                                +realRadius
                                            ) + currentLatitude,
                                            random.nextDouble(
                                                -realRadius,
                                                +realRadius
                                            ) + currentLongitude
                                        )
                                    )
                                }
                                gameRef.update("locations", allLocations)
                                setAllMarkers(allLocations)
                            }
                        }
                    } else {
                        Log.d("error", "Current location is null. Using defaults.")
                        Log.e("error", "Exception: %s", task.exception)

                        prepareGame() // try again
                        Log.i("location", "trying again")
                    }
                }

                var mLocationRequest = LocationRequest()
                mLocationRequest.interval = 30000
                mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

                fusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty() && gameStarted && !gameOver) {
                //The last location in the list is the newest
                val location = locationList.last()
                Log.i("MapsActivity", "Location: " + location.latitude + " " + location.longitude)
                val latLng = LatLng(location.latitude, location.longitude)

                // compare with all markers
                for (element in allMarkers) {
                    if (calculateDistance(latLng, element.position) < 0.00045) { // within 100m
                        checkLocation(element)
                    }
                }
            }
        }
    }

    private fun checkLocation(marker: Marker) {
        if(!findLocationIn(marker.position, foundLocations)) { // check if already discovered this location
            if (findLocationIn(marker.position, enemyChests)) { // check if there is a chest
                foundChests++
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.chest_131982518657265365_24))
                if (foundChests >= chestNumber) {

                    val newFragment = ScavengeGameOverFragment()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainer, newFragment).commit()


                    val winnerString = if (lobbyOwner) {
                        "player1_winner"
                    } else {
                        "player2_winner"
                    }

                    gameRef.update(mapOf(
                        "game_finished" to true,
                        winnerString to true))

                    finishGame(true)

                }
            } else {
                marker.remove()
            }
            foundLocations.add(marker.position)
        }
    }

    private fun setAllMarkers(allLocations: MutableList<LatLng>) {
        val markerOptions =
            MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.crossedbones))
        markerOptions.anchor(0.5f, 0.5f)

        for (i in 0 until allLocations.size) {
            allMarkers.add(
                map.addMarker(
                    markerOptions.position(
                        allLocations[i]
                    )
                )
            )
        }
    }

    private fun calculateDistance(myPosition: LatLng, markerPosition: LatLng): Double {
        val x1 = myPosition.latitude
        val y1 = myPosition.longitude
        val x2 = markerPosition.latitude
        val y2 = markerPosition.longitude

        return sqrt((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0))
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
        // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
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



    // TODO: fix overlay
    // TODO: add shop functionality
    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    fun startGame() {
        gameStarted = true
        if (lobbyOwner) {
            gameRef.update(
                mapOf(
                    "player1_locations" to myChests,
                    "game_started" to true
                )
            )

        } else {
            gameRef.update("player2_locations", myChests)
        }

        binding.shopButton.text = playerCoins.toString()
        for (marker in allMarkers) {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.crossedbones))
        }
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun findLocationIn(thisLocation: LatLng, container: MutableList<LatLng>): Boolean {
        for (chestLocation in container) {
            if (thisLocation == chestLocation) {
                return true
            }
        }

        return false
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if (!gameStarted) {

            if (findLocationIn(p0!!.position, myChests)) {
                myChests.remove(p0)
                viewModel.increaseRemaingChests()
                p0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.crossedbones))
                gameReady = false
                if (lobbyOwner) {
                    gameRef.update("player1_ready", false)
                } else {
                    gameRef.update("player2_ready", false)
                }
                return false
            } else {
                if (myChests.size < chestNumber) {
                    myChests.add(p0.position)
                    viewModel.decreaseRemainingChests()
                    p0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.chest_131982518657265365_24))
                    if (myChests.size >= chestNumber) { // if all available chests were placed
                        gameReady = true
                        if (lobbyOwner) {

                            gameRef.update("player1_chests", myChests).addOnSuccessListener {
                                gameRef.update("player1_ready", true)
                            }

                        } else {
                            gameRef.update("player2_chests", myChests).addOnSuccessListener {
                                gameRef.update("player2_ready", true)
                            }
                        }
                    }
                } else {
                    val toasty =
                        Toast.makeText(this, "You've got no more chests!", Toast.LENGTH_LONG)
                    toasty.setGravity(Gravity.CENTER, 0, 0)
                    toasty.show()

                }
            }

            selectBinding.remainingChests.text = (chestNumber - myChests.size).toString()
        }
        return false
    }

    fun finishGame(wonGame: Boolean) {

        // TODO: add more game over stuff
        gameOver = true
        if (wonGame) {

            gameOverBinding.endGameTitle.text = "VICTORY"
        } else {
            gameOverBinding.endGameTitle.text = "DEFEAT"
            gameOverBinding.endGameMessage.text = "Arr! They found all our treasures!"
        }

        val view = gameOverBinding.root
        addContentView(
            view, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    fun isGameReady(): Boolean {
        return gameReady && enemyReady
    }

    fun stealBack() {

    }

    fun getDirections() {

    }

    fun chestReveal() {
        /*var revealedChest = false
        while(!revealedChest) {
            for (element in enemyChests) {
                checkLocation(element)
            }
        }*/
    }

}