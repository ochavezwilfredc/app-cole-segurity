package com.app.movilidad.Clases

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.collections.ArrayList


class ClsLocalizacion : OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{


    var gmap:GoogleMap? = null
    var permisoLocalizacion: Boolean = false
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 1
    val LOCATION_PERMISSION_REQUEST_CODE = 1
    var ultimaLocalizacion: Location?=null
    val DEFAULT_ZOOM = 17f
    var locationClient:FusedLocationProviderClient?= null
    var activity:AppCompatActivity? = null
     var defaultClick:Boolean = true
     var markers = ArrayList<Marker>()

    var clickMarker:Boolean = false
    var markerOrigen:Marker?=null
    var markerDestino:Marker?=null
    constructor(activity: AppCompatActivity?) {
         this.activity = activity
         activarLocalizacion()
        /*
         val locationManager: LocationManager? = activity?.getSystemService(LOCATION_SERVICE) as LocationManager?
         try {
                 // Request location updates
             locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
         } catch(ex: SecurityException) {

         }
        */
     }


     companion object {
         var lastLatLong :LatLng? = null
     }

     override fun onMapReady(gmaps: GoogleMap) {
        gmap =gmaps
        gmap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        gmap?.clear()
         habilitarLocalizacion()
         gmap?.setOnMapClickListener {
             if(clickMarker){
                 gmap!!.clear()
                 lastLatLong = it
                 markerChofer(lastLatLong!!)
             }

         }

         if(lastLatLong!=null){
             gmap!!.clear()
             gmap!!.addMarker( MarkerOptions()
                 .position(lastLatLong!!))
             gmap!!.moveCamera(
                 CameraUpdateFactory.newLatLngZoom(lastLatLong, DEFAULT_ZOOM))
         }


     }

    fun addMarker(latlon:LatLng){
        gmap!!.clear()
        markerChofer(latlon)
        gmap!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(latlon, DEFAULT_ZOOM))

    }
     fun agregarMarcador(marker:Marker){
         markers.add(marker)
         val builder = LatLngBounds.Builder()
         for (m in markers) {
             builder.include(m.position)
         }
         val bounds = builder.build()
         val padding = 20
         val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
         gmap!!.moveCamera(cu)
     }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
             ClsLocalizacion.lastLatLong = LatLng(location.latitude, location.longitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun buscarGeoLocalizacion(){

        if (gmap != null) {
            val act : AppCompatActivity = activity as AppCompatActivity
            locationClient!!.lastLocation
                .addOnCompleteListener(act) { task ->
                    if (task.isSuccessful && task.result != null) {
                        ultimaLocalizacion = task.result
                        val long = ultimaLocalizacion?.longitude
                        val lat = ultimaLocalizacion?.latitude
                        val latlong = LatLng(lat!!,long!!)
                        lastLatLong = latlong


                        markers.add(markerChofer(latlong))
                        gmap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(latlong, DEFAULT_ZOOM))


                    } else {

                        //no se encontro
                    }
                }
            // Access to the location has been granted to the app.
            gmap!!.setMyLocationEnabled(true)
        }
    }
     fun markerChofer(coor:LatLng):Marker{
         markerOrigen =  gmap!!.addMarker(MarkerOptions()
                 .position(coor)
                 .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
         return markerOrigen!!
     }

    fun markerDestino(coor:LatLng):Marker{
        markerDestino =  gmap!!.addMarker(MarkerOptions()
            .position(coor)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
        return markerDestino!!
    }

    fun activarLocalizacion(){
        val act : AppCompatActivity = activity as AppCompatActivity
        locationClient = LocationServices.getFusedLocationProviderClient(act)
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            PermissionUtils.requestPermission( act  , LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false)
        }
    }
    fun habilitarLocalizacion(){
        activarLocalizacion()
        buscarGeoLocalizacion()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (PermissionUtils.isPermissionGranted(
                permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            buscarGeoLocalizacion()
        } else {
            permisoLocalizacion = true
        }

    }



}