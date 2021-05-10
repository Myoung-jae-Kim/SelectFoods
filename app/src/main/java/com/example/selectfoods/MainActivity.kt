package com.example.selectfoods

import android.content.ContentValues.TAG
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationListener
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
//import com.google.android.gms.location.LocationServices
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.Permissions

//get hash key value
/*    try {
        val info = packageManager.getPackageInfo(
                "com.example.selectfoods", // TODO Change the package name
                PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }
    } catch (e: PackageManager.NameNotFoundException) {

    } catch (e: NoSuchAlgorithmException) {

    }*/

//get current location
/* val button_loc = findViewById<Button>(R.id.getlocation)
            button_loc.setOnClickListener {
                val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                //Manifest에 권한이 추가되어 있어도 한번 더 확인
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
                } else {
                    when { //프로바이더 제공자 활성화 여부 체크
                        isNetworkEnabled -> {
                            val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) //인터넷 기반으로 위치 탐색
                            getLongitude = location?.longitude!!
                            getLatitude = location.latitude
                            toast("현재위치를 불러옵니다.")
                        }
                        isGPSEnabled -> {
                            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) //GPS 기반으로 위치 탐색
                            getLongitude = location?.longitude!!
                            getLatitude = location.latitude
                            toast("현재위치를 불러옵니다.")
                        }
                        else -> {

                        }
                    }
                    //몇초 간격과 몇 미터를 이동했을 시에 호출 되는 부분 - 주기적으로 위치 업데이트를 하고 싶다면~
                    //주기적으로 업데이트 사용안할 시에 반드시 해제
                   *//* lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1F, gpsLocationListener)

                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1F, gpsLocationListener)

                    lm.removeUpdates(gpsLocationListener)*//*
                }
            }
        lm.removeUpdates(gpsLocationListener)

        //주기적으로 위치 업데이트 안할거면 사용X
        val  gpsLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val provider: String = location.provider
                val longitude: Double = location.longitude
                val latitude: Double = location.latitude
                val altitude: Double = location.altitude
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }*/



class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val button = findViewById<Button>(R.id.choice)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

    }
}




