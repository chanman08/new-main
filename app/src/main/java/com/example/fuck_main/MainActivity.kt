package com.example.fuck_main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.schedule

@SuppressLint("StaticFieldLeak")
val db = Firebase.firestore

var aId = "0"
var tStamp = "0"
var sId = "0"

/*bus stop lat-lng array zone*/
var bus_stop_locate = arrayOfNulls<LatLng>(10)

/*bus locate lat-lng array zone*/
var bus_locate= arrayOfNulls<LatLng>(8)

/*Arrival Time id array zone*/
var arrival_time_id = arrayOfNulls<String>(8)

/*bus time array zone*/
var bus_time_info = arrayOfNulls<String>(10)

/*Time stamp array zone*/
var stamp_of_time = arrayOfNulls<Int>(4)

/*Station id array zone*/
var id_of_station=arrayOfNulls<String>(4)


class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var callback: LocationCallback


    //Test SW TF
    var test_sw = false
    //map setting
    private lateinit var mMap: GoogleMap

    //first moving
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // test button first moving
        val getButton: ToggleButton = findViewById(R.id.toggleButton)
        var timerCallback1: TimerTask.() -> Unit = {
            fetchLatestLocation()
            reload_map(mMap)
        }


        // test button tf checking
        getButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                test_sw = true
                Timer().schedule(0, 1000, timerCallback1)
            } else if (!isChecked) {
                test_sw = false
            }
        }

        callback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
            }
        }
    }

    // first map drawer
    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null)
        {
            mMap = googleMap
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            return
        }
        val last_locate = fusedLocationProviderClient.lastLocation
        last_locate.addOnSuccessListener {

            //now location
            var now_pojit = LatLng(it.latitude, it.longitude)


            //drawing now location point
            mMap.addMarker(MarkerOptions().position(now_pojit).title("現在地")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fuck)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now_pojit, 20f))
        }
        val locationRequest = createLocationRequest() ?: return
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            callback,
            null
        )
    }

    //reload map drawer
    private fun reload_map(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mMap = googleMap
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            return
        }
        val ll = fusedLocationProviderClient.lastLocation
        ll.addOnSuccessListener {
            var now_posit = LatLng(it.latitude, it.longitude)
            mMap.clear()

            // now position redrawing
            mMap.addMarker(MarkerOptions().position(now_posit).title("現在地")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fuck)))

            // now bus position redrawing
                if(bus_locate[0]!=null) {
                    mMap.addMarker(
                        MarkerOptions().position(bus_locate[0]).title("バス" + "1")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon))
                    )
                }


            // bus stop location & limit second
            for (i in 0..9)
            {
                if(bus_stop_locate[i]!=null&& bus_time_info!=null) {
                    mMap.addMarker(
                        MarkerOptions().position(bus_stop_locate[i])
                            .title("バス停" + i + "まであと" + bus_time_info[i])
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop))
                    )
                }
            }
        }
    }

    private fun fetchLatestLocation() {
        val latestLocation = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                101
            )
        } else if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
        } else if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                101
            )
        } else {
            latestLocation.addOnSuccessListener {

                if (test_sw) {
                    if (it != null) {
                        Toast.makeText(
                            this,
                            "${it.latitude} \n ${it.longitude}",
                            Toast.LENGTH_SHORT
                        ).show()
                        db.collection("bus")
                            .document("gpucZzxPYDlp5sWXhQOO")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document != null && document.data != null) {
                                        aId = document.data?.get("arrivalTimeId").toString()
                                        val geoPoint = document.getGeoPoint("locate")
                                        sId = document.data?.get("stationsId").toString()
                                        tStamp = document.data?.get("timeStamp").toString()
                                        val lat = geoPoint!!.latitude
                                        val lng = geoPoint!!.longitude
                                        bus_locate[0] = LatLng(lat, lng)
                                    }
                                }
                            }
                        db.collection("arrivalTimes")
                            .document(sId)
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    for(i in 1..9) {
                                        if (document != null && document.data != null) {
                                            var bus_time_name: String = "station$i"
                                            println(bus_time_name)
                                            println(document.data?.get(bus_time_name).toString())
                                            bus_time_info[i] = document.data?.get(bus_time_name).toString()
                                        }
                                    }
                                }
                            }
                        db.collection("stations")
                            .document("nagakurasen_up_805")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    for(i in 1..9) {
                                        if (document != null && document.data != null) {
                                            var geopoint_name: String = "station"+(i.toString())
                                            val geoPoint =
                                                document.data?.get(geopoint_name) as List<*>
                                            val lat = (geoPoint[1] as GeoPoint)!!.latitude
                                            val lng = (geoPoint[1] as GeoPoint)!!.longitude
                                            bus_stop_locate[i] = LatLng(lat, lng)
                                        }
                                    }
                                }
                            }
                    }
                    else if (!test_sw) {
                        println("fuck")
                    }
                    val locationRequest = createLocationRequest() ?: return@addOnSuccessListener
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        callback,
                        null
                    )
                }
            }

        }
    }
    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}