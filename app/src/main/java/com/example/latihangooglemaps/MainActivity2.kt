package com.example.latihangooglemaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.latihangooglemaps.databinding.ActivityMain2Binding
import com.example.latihangooglemaps.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity2 : AppCompatActivity(), OnMapReadyCallback {

    var cevestLocationMarker: Marker? = null
    var garisJarak:Polyline? = null

    lateinit var mLocationRequest: LocationRequest
    lateinit var mMap: GoogleMap
    var mLastLocation: Location? = null
    var mCurrentLocationMarker: Marker? = null

    internal var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityMain2Binding

    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            val locationList = locationResult?.locations
            if(locationList!!.isNotEmpty()){
                //Lokasi terakhir adalah yg terbaru
                val location = locationList.last()
                Log.i("MyLocation", location.toString())

                mLastLocation = location
                if(mCurrentLocationMarker !=null)
                {
                    mCurrentLocationMarker?.remove()
                }

                if(cevestLocationMarker !=null)
                {
                    cevestLocationMarker?.remove()
                }
                if (garisJarak !=null)
                {
                    garisJarak?.remove()
                }
                //Letakkan marker pada posisi kita yang terbaru
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Posisi Sekarang "
                        +location.latitude.toString()
                        +","+location.longitude.toString())
                markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                //menattach markerOptions ke mMap
                mCurrentLocationMarker = mMap.addMarker(markerOptions)
                //memfokuskan camera ke posisi saat ini
                mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(latLng, 15.0f))
//                binding.textView.text = getAddress(location.latitude, location.longitude)

                val cevestLocation = LatLng(-6.234746089365719, 106.99044492873139)
                //Menambahkan marker untuk cevest
                cevestLocationMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(cevestLocation)
                        .title("BBPVP BEKASI - Kemnaker RI")
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
                //Menampilkan title tanpa klik
                val circleOptions = CircleOptions()
                circleOptions.center(cevestLocation)
                //Radius, satuan meter
                circleOptions.radius(1000.0)
                circleOptions.strokeColor(Color.BLUE)
//                circleOptions.fillColor(Color.RED)
                mMap.addCircle(circleOptions)

                val distance = FloatArray(2)
                //Mendapatkan jarak antara satu titik dengan titik yang lain
                Location.distanceBetween(
                    cevestLocation.latitude, cevestLocation.longitude,
                    location.latitude, location.longitude,
                    distance
                )
                var status = ""
                //Mengcheck jika current position masih didalam area
                if (distance[0] > circleOptions.radius)
                {
                    status = "Diluar Area Kantor"
                }else{
                    status = "Didalam Area Kantor"
                }

                //Menambahkan garis pada titik yang tersambung
                garisJarak = mMap.addPolyline(
                    PolylineOptions().add(cevestLocation,
                        LatLng(location.latitude, location.longitude))
                        .width(5.0f)
                        .color(Color.BLACK)
                )
                //Menyiapkan text untuk ditampilkan ke dalam textview
                var sourceText = "<b>Lokasi saat ini</b> : "+
                getAddress(location.latitude, location.longitude)+
                        "<br/> <b>Status</b> :${status}"
                binding.textView.text = Html.fromHtml(sourceText)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Mendapatkan data lokasi saat ini
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    fun getAddress(lat:Double, lng:Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        Log.i("List",list.toString())
        return list?.get(0)!!.getAddressLine(0)
    }

    override fun onMapReady(googleMap: GoogleMap) {
//        val BBPVP = LatLng(-6.234759018623491, 106.99047043528832)
//        val BksTCM2 = LatLng(-6.258286675177583, 107.02132887179972)
//        val PondokTimurIndah = LatLng(-6.2801840174288355, 107.01909949632706)
//        val GreenWalkXXI = LatLng(-6.2618501752255025, 107.01934374868496)
//        val TamanNirwanaBekasiTimur = LatLng(-6.27559795424682, 107.01581119559323)
//
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(BBPVP)
//                .title("Marker in BBPVP BEKASI")
//        )
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(BksTCM2)
//                .title("Marker in Bekasi Trade Center Mall 2")
//        )
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(PondokTimurIndah)
//                .title("Marker in Pondok Timur Indah")
//        )
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(GreenWalkXXI)
//                .title("Marker in Green Walk XXI")
//        )
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(TamanNirwanaBekasiTimur)
//                .title("Marker in Taman Nirwana Bekasi Timur")
//        )
//        //posisi fokus apk pada titik yang didefinisikan
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BBPVP, 15f))
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 20000

        mLocationRequest.priority = LocationRequest
            .PRIORITY_BALANCED_POWER_ACCURACY
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
            return Toast.makeText(this, "Izinkan  penggunaan memakai location", Toast.LENGTH_SHORT).show()
        }
        mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        googleMap.isMyLocationEnabled = true
    }
}
