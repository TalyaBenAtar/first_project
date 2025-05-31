package com.example.first_project.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.first_project.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var pendingLat: Double? = null
    private var pendingLon: Double? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        if (pendingLat != null && pendingLon != null) {
            zoom(pendingLat!!, pendingLon!!)
            pendingLat = null
            pendingLon = null
        } else {

            // Otherwise, show default view
            val defaultLocation = LatLng(32.1093, 34.8555)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        }
    }

    fun zoom(lat: Double, lon: Double) {
        if (googleMap == null) {

            // Store for later if map is not ready yet
            pendingLat = lat
            pendingLon = lon

        } else {
            val location = LatLng(lat, lon)
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(location).title("Score Location"))
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }


}