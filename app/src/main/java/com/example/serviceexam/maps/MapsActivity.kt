package com.example.serviceexam.maps

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.*
import com.example.serviceexam.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 */
private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.FOREGROUND_SERVICE
)

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mGoogleMap: GoogleMap
    var mapFrag: SupportMapFragment? = null
    lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val mMarkers: HashMap<String, Marker> = HashMap()
    private var latLng: LatLng? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.getReference("locations")
    private var mLocationCallback: LocationCallback? = null

    private var listLocationsToSave: List<String?> = ArrayList()

    private lateinit var trackerService: TrackerService
    private var delay: Long = 0

    private val workManager = WorkManager.getInstance(application)

    private val gsonPretty = GsonBuilder().setPrettyPrinting().create()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check GPS is enabled
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
        }

        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)

        switch_tracking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startTrackerService()
                createFile("text/plain", "TrackerAndroid10")
            } else {
                unbindService(connection)
                mBound = false
                listLocationsToSave = trackerService.stopTracker()
                Toast.makeText(applicationContext, "Tracking is disable", Toast.LENGTH_LONG).show()
                fetchData()
                editDocument()
            }
        }
        checkPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        if (!hasPermissions(baseContext)) {
            this.requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }

    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TrackerService.LocalBinder
            trackerService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    override fun onPause() {
        super.onPause()
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    private fun startTrackerService() {
        Intent(this, TrackerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        loginToFirebase()
        setupMap()

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mGoogleMap.isMyLocationEnabled = true
            } else {
                checkPermissions()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
            mGoogleMap.isMyLocationEnabled = true
        }
    }

    private fun setupMap() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {
                    val location = locationList.last()
                    Log.i(
                        "MapsActivity",
                        "Location: " + location.latitude + " " + location.longitude
                    )
                    mLastLocation = location

                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker?.remove()
                    }

                    latLng = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun loginToFirebase() {
        val email = getString(R.string.firebase_email)
        val password = getString(R.string.firebase_password)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                subscribeToUpdates()
                Log.d(TAG, "firebase auth success")
            } else {
                Log.d(TAG, "firebase auth failed")
            }
        }
    }

    //Read from database
    private fun subscribeToUpdates() {
        myRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {
                if (p0 != null) {
                    setMarker(p0)
                }
            }

            override fun onChildChanged(p0: DataSnapshot, previousChildName: String?) {
                setMarker(p0)
            }

            override fun onChildMoved(p0: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onCancelled(error: DatabaseError) {
                Log.d(
                    TAG,
                    "Failed to read value.",
                    error.toException()
                )
            }
        })
    }

    private fun setMarker(dataSnapshot: DataSnapshot) {
        val key = dataSnapshot.key
        //val value = dataSnapshot.value as HashMap<String, Any>?
        val lat = latLng!!.latitude
        val lng = latLng!!.longitude
        val location = LatLng(lat, lng)
        if (!mMarkers.containsKey(key)) {
            key?.let {
                mMarkers.put(
                    it,
                    mGoogleMap.addMarker(MarkerOptions().title(key).position(location))
                )
            }
        } else {
            mMarkers[key]?.setPosition(location)
        }
        val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        for (marker in mMarkers.values) {
            builder.include(marker.position)
        }
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(applicationContext, "Permission request granted", Toast.LENGTH_LONG)
                    .show()
                mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mGoogleMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(applicationContext, "Permission request denied", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun createFile(mimeType: String, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)

            // Create a file with the requested MIME type.
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    private fun editDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
        }

        startActivityForResult(intent, EDIT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.normalizeScheme()?.let { alterDocument(it) }
        }
    }

    private fun alterDocument(uri: Uri) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    val listToSave = gsonPretty.toJson(listLocationsToSave)
                    it.write(
                        (listToSave)?.toByteArray()
                    )
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun fetchData() {
        calculateTime()

        val constraints: Constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncDataWork =
            PeriodicWorkRequest.Builder(Worker::class.java, 2, TimeUnit.HOURS)
                .addTag(TAG)
                //Calcular horas para las 9 am
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .setInputData(createInputDataForWorker())
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MAX_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

        workManager.enqueueUniquePeriodicWork(
            "AlarmLocations",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicSyncDataWork
        )
    }

    private fun createInputDataForWorker(): Data {
        val builder = Data.Builder().apply {
            listLocationsToSave.forEach{
                putString("Locations", it)
            }
            val arrayOut: Array<String?> = listLocationsToSave.toTypedArray()
            putStringArray("List", arrayOut)
        }
        return builder.build()
    }

    private fun calculateTime() {
        val currentTime = System.currentTimeMillis()
        val zoneTime = TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
        val format: DateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.US
        )
        format.timeZone = zoneTime

        val calendar = Calendar.getInstance()
        calendar.timeZone = zoneTime

        calendar.set(Calendar.HOUR_OF_DAY, SELF_REMINDER_HOUR)
        calendar.set(Calendar.MINUTE, SELF_REMINDER_MINUTES)
        calendar.set(Calendar.SECOND, 0)
        val newTime = calendar.timeInMillis

        delay = if ((newTime - currentTime) > 0) {
            newTime - currentTime
        } else {
            0
        }
    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        private const val WRITE_REQUEST_CODE: Int = 43
        private const val EDIT_REQUEST_CODE: Int = 44

        private const val SELF_REMINDER_HOUR = 9
        private const val SELF_REMINDER_MINUTES = 10

    }
}
