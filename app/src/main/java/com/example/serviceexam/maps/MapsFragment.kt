package com.example.serviceexam.maps

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.example.serviceexam.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


/**
 * A simple [Fragment] subclass.
 */
private const val MY_LOCATION_REQUEST_CODE = 1
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

@Suppress("DEPRECATION")
class MapsFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLocationPermission();
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (getString(R.string.maps_api_key).isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Add your own API key in MapWithMarker/app/secure.properties as MAPS_API_KEY=YOUR_API_KEY",
                Toast.LENGTH_LONG
            ).show()
        }
        val mapFragment =
            FragmentActivity().supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        mMap?.isMyLocationEnabled = true;
        mMap?.setOnMyLocationButtonClickListener(this);
        mMap?.setOnMyLocationClickListener(this);
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        mMap?.apply {
            val sydney = LatLng(p0.latitude, p0.longitude)
            addMarker(
                MarkerOptions()
                    .position(sydney)
                    .title("Home")
            )
            moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(MY_LOCATION_REQUEST_CODE)
    fun requestLocationPermission() {
        val perms =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(requireContext(), "Permission already granted", Toast.LENGTH_SHORT).show()
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                "Please grant the location permission",
                MY_LOCATION_REQUEST_CODE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }
}
