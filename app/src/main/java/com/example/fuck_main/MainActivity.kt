package com.example.fuck_main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
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
var bLocate = LatLng(0.0,0.0)
var bs1Locate = LatLng(0.0,0.0)
var bs2Locate = LatLng(0.0,0.0)
var bs3Locate = LatLng(0.0,0.0)
var bs4Locate = LatLng(0.0,0.0)
var bs5Locate = LatLng(0.0,0.0)
var bs6Locate = LatLng(0.0,0.0)
var bs7Locate = LatLng(0.0,0.0)
var bs8Locate = LatLng(0.0,0.0)
var bs9Locate = LatLng(0.0,0.0)

/*bus stop lat-lng array zone*/
var bus_stop_locate = arrayOfNulls<LatLng>(10)

/*bus locate lat-lng*/
var bus_locate= arrayOfNulls<LatLng>(12)



var tStamp = "0"
var sId = "0"
val sList = arrayOf("")

class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var callback: LocationCallback
    var a = 0
    private val handler = Handler()
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //val mHandler = Handler()
        var x = 0
        val getButton: ToggleButton = findViewById(R.id.toggleButton)
        var timerCallback1: TimerTask.() -> Unit = {

            var str = Integer.toString(x)
            //mHandler.post{messageView.text = str}
            fetchLatestLocation()
            reload_map(mMap)
            System.out.println(str)
            x++
        }

        getButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                a = 1
                Timer().schedule(0, 1000, timerCallback1)
            } else if (!isChecked) {
                a = 0
            }
        }

        callback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
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
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val ll = fusedLocationProviderClient.lastLocation
        ll.addOnSuccessListener {
            var now_pojit = LatLng(it.latitude, it.longitude)
            mMap.addMarker(MarkerOptions().position(now_pojit).title("現在地")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.round40)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now_pojit, 20f))
        }
        val locationRequest = createLocationRequest() ?: return
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            callback,
            null
        )
    }
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
            var now_pojit = LatLng(it.latitude, it.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(now_pojit).title("現在地")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.round40)))
            mMap.addMarker(MarkerOptions().position(bLocate).title("バス")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon)))

            mMap.addMarker(MarkerOptions().position(bs1Locate).title("バス停1")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs2Locate).title("バス停2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs3Locate).title("バス停3")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs4Locate).title("バス停4")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs5Locate).title("バス停5")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs6Locate).title("バス停6")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs7Locate).title("バス停7")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs8Locate).title("バス停8")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))
            mMap.addMarker(MarkerOptions().position(bs9Locate).title("バス停9")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)))


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

                if (a == 1) {
                    if (it != null) {
                        Toast.makeText(
                            this,
                            "${it.latitude} \n ${it.longitude}",
                            Toast.LENGTH_SHORT
                        ).show()
                        /*val radioGroup = findViewById<RadioGroup>(R.id.RadioGroup)
                        val id = radioGroup.checkedRadioButtonId
                        val checkedRadioButton = findViewById<RadioButton>(id)
                        val user = hashMapOf(
                            "latitude" to "${it.latitude}",
                            "longitude" to "${it.longitude}",
                            "going" to checkedRadioButton.text
                        )
                        println(checkedRadioButton.text)

                         */
                        // Add a new document with a generated ID
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
                                        bLocate = LatLng(lat, lng)
                                    }
                                }
                            }
                        db.collection("arrivalTimes")
                            .document(sId)
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document != null && document.data != null) {
                                        val messageViewA = findViewById<TextView>(R.id.textView2)
                                        messageViewA.text = document.data?.get("station3").toString()
                                        val messageViewB: TextView = findViewById(R.id.textView3)
                                        messageViewB.text = document.data?.get("station4").toString()
                                        val messageViewC: TextView = findViewById(R.id.textView4)
                                        messageViewC.text = document.data?.get("station5").toString()
                                        val messageViewD: TextView = findViewById(R.id.textView5)
                                        messageViewD.text = document.data?.get("station6").toString()
                                        val messageViewE: TextView = findViewById(R.id.textView6)
                                        messageViewE.text = document.data?.get("station7").toString()
                                        val messageViewF: TextView = findViewById(R.id.textView7)
                                        messageViewF.text = document.data?.get("station8").toString()
                                        val messageViewG: TextView = findViewById(R.id.textView8)
                                        messageViewG.text = document.data?.get("station9").toString()
                                        val messageViewH: TextView = findViewById(R.id.textView9)
                                        messageViewH.text = document.data?.get("station10").toString()

                                    }
                                }
                            }
                        db.collection("stations")
                            .document("dummyLine")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document != null && document.data != null) {
                                        val Point1 = document.data?.get("station1")as List<*>
                                        println(Point1[1])
                                        val geoPoint1=Point1[1] as GeoPoint
                                        val lat1 = geoPoint1!!.latitude
                                        val lng1 = geoPoint1!!.longitude
                                        println(lat1)
                                        println(lng1)
                                        bs1Locate = LatLng(lat1, lng1)

                                        val geoPoint2 = document.data?.get("station2")as List<*>
                                        val lat2 = (geoPoint2[1]as GeoPoint)!!.latitude
                                        val lng2 = (geoPoint2[1]as GeoPoint)!!.longitude
                                        bs2Locate = LatLng(lat2, lng2)

                                        val geoPoint3 = document.data?.get("station3")as List<*>
                                        val lat3 = (geoPoint3[1]as GeoPoint)!!.latitude
                                        val lng3 = (geoPoint3[1]as GeoPoint)!!.longitude
                                        bs3Locate = LatLng(lat3, lng3)

                                        val geoPoint4 = document.data?.get("station4")as List<*>
                                        val lat4 = (geoPoint4[1]as GeoPoint)!!.latitude
                                        val lng4 = (geoPoint4[1]as GeoPoint)!!.longitude
                                        bs4Locate = LatLng(lat4, lng4)

                                        val geoPoint5 = document.data?.get("station5")as List<*>
                                        val lat5 = (geoPoint5[1]as GeoPoint)!!.latitude
                                        val lng5 = (geoPoint5[1]as GeoPoint)!!.longitude
                                        bs5Locate = LatLng(lat5, lng5)

                                        val geoPoint6 = document.data?.get("station6")as List<*>
                                        val lat6 = (geoPoint6[1]as GeoPoint)!!.latitude
                                        val lng6 = (geoPoint6[1]as GeoPoint)!!.longitude
                                        bs6Locate = LatLng(lat6, lng6)

                                        val geoPoint7 = document.data?.get("station7")as List<*>
                                        val lat7 = (geoPoint7[1]as GeoPoint)!!.latitude
                                        val lng7 = (geoPoint7[1]as GeoPoint)!!.longitude
                                        bs7Locate = LatLng(lat7, lng7)

                                        val geoPoint8 = document.data?.get("station8")as List<*>
                                        val lat8 = (geoPoint8[1]as GeoPoint)!!.latitude
                                        val lng8 = (geoPoint8[1]as GeoPoint)!!.longitude
                                        bs8Locate = LatLng(lat8, lng8)

                                        val geoPoint9 = document.data?.get("station9")as List<*>
                                        val lat9 = (geoPoint9[1]as GeoPoint)!!.latitude
                                        val lng9 = (geoPoint9[1]as GeoPoint)!!.longitude
                                        bs9Locate = LatLng(lat9, lng9)
                                    }
                                }
                            }
                    } else if (a == 0) {
                        println("fuck")
                    }
                    println(aId)
                    println(bLocate)
                    println(sId)
                    println(tStamp)
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