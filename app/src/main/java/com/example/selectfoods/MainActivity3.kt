package com.example.selectfoods

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPoint
import java.lang.NullPointerException

/*
class LocationHelper {

    val LOCATION_REFRESH_TIME = 3000 // 3 seconds. The Minimum Time to get location update
    val LOCATION_REFRESH_DISTANCE = 30 // 30 meters. The Minimum Distance to be changed to get location update
    val MY_PERMISSIONS_REQUEST_LOCATION = 100

    var myLocationListener: MyLocationListener? = null

    interface MyLocationListener {
        fun onLocationChanged(location: Location)
    }

    fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
        myLocationListener = myListener

        val mLocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

        val mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                //your code here
                myLocationListener!!.onLocationChanged(location) // calling listener to inform that updated location is available
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
// check for permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME.toLong(),LOCATION_REFRESH_DISTANCE.toFloat(), mLocationListener)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(context as Activity,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // permission is denined by user, you can show your alert dialog here to send user to App settings to enable permission
            } else {
                ActivityCompat.requestPermissions(context,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),MY_PERMISSIONS_REQUEST_LOCATION)
            }
        }
    }

}
*/


class MainActivity3 : AppCompatActivity() {
    /*private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var toggleButton: ToggleButton*/
    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationListener? = null
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

 /*       toggleButton = findViewById(R.id.toggle1) as ToggleButton

        //LocationManager 객체 생성
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        toggleButton.setOnClickListener {
            try {
                if (toggleButton.isChecked()) {
                    //gps 제공자의 정보가 바뀌면 콜백 리스너
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener)
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener)
                } else {
                    locationManager.removeUpdates(mLocationListener) // 미수신시 자원 해제
                }
            } catch (ex : SecurityException) {

            }
        }
*/
/*        mLocationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
        mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                var lat = 0.0
                var lng = 0.0
                if (location != null) {
                    lat = location.latitude
                    lng = location.longitude
                    Log.d("lat + lng", "Lat: ${lat} , lng: ${lng}")
                }
                //var currentLocation = LatLng(lat, lng)

                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lng), true)
            }
        }*/

        var toggleButton = findViewById(R.id.toggle1) as ToggleButton
        val mapView = MapView(this)
        val  marker = MapPOIItem()

        val mapViewContainer = findViewById<ConstraintLayout>(R.id.map_view) as ViewGroup
        mapViewContainer.addView(mapView)

        //버튼 클릭하면 현재 위치로
        toggleButton.setOnClickListener {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val lm : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                try {
                    val userNowLocation : Location? =
                        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    val  uLatitude = userNowLocation!!.latitude
                    val  uLongitude = userNowLocation!!.longitude
                    val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude,uLongitude)
                    mapView.setMapCenterPoint(uNowPosition,true)

                    marker.itemName = "내 위치"
                    marker.tag = 0
                    marker.mapPoint = uNowPosition
                    marker.markerType = MapPOIItem.MarkerType.BluePin

                    //마커를 클릭했을 때
                    marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    mapView.addPOIItem(marker)

                } catch (e: NullPointerException) {
                    Log.e("Location_error", e.toString())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityCompat.finishAffinity(this)
                    } else {
                        ActivityCompat.finishAffinity(this)
                    }

                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
                    System.exit(0)
                }
            } else {
                Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }
        }

    }
}