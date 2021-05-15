package com.example.selectfoods

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.selectfoods.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main3.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import java.lang.NullPointerException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity3 : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK b50fe2e41bb5f4ff3197aa3c62c0c67f" // Rest api 키
    }

//    private lateinit var binding : ActivityMainBinding
    private val ACCESS_FINE_LOCATION = 1000
    private val listItems = arrayListOf<ListItem>() //검색결과를 담는 리스트
//    private lateinit val mapView : MapView
    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationListener? = null
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    val marker = MapPOIItem()
    var uLongitude : Double = 0.0 //x
    var uLatitude : Double = 0.0 //y


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // binding = ActivityMainBinding.inflate()
       // val view = binding.root
        setContentView(R.layout.activity_main3)
        //setContentView(view)

        //위치추적 버튼
        btn_start.setOnClickListener {
            if (checkLocationService()) {
                //GPS가 켜져있을 때
                permissionCheck()
            } else {
                //GPS가 꺼져있을 때
                Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

        //검색 시작
        btn_search.setOnClickListener {
            searchKeyword("식당",uLongitude,uLatitude,1000)
        }

    }

    //위치 권한 확인
    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //권한 거절 (한번 더 요청)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->
                    
                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    //최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                } else {
                    //다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val  intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${packageName}"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            //권한이 있는 상태
            startTracking()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //권한 요청 후 승인 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                //권한 요청 후 거절됨 (재 요청)
                Toast.makeText(this, "위치 권한이 거절되었습니다.", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    //GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    data class  Uposition(var x: Double, var y: Double)

    private fun getCurrentLocation(): Uposition {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm : LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            try {
                val userNowLocation : Location? =
                    lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                uLatitude = userNowLocation!!.latitude
                uLongitude = userNowLocation!!.longitude

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
        return Uposition(uLatitude, uLongitude) //Long 이 X고 Lati 가 Y 인데...왜 mapPointWithGeoCoord 에서는 반대로 해야 내 위치가 정확히 나올까...?
    }

    private fun startTracking() {
        val (x, y) = getCurrentLocation()
   //     Log.d("DoubletoStringmyPosition", x.toString() + " " + y.toString())
        val uNowPosition = MapPoint.mapPointWithGeoCoord(x,y)
        mapView.setMapCenterPoint(uNowPosition,true)

        marker.itemName = "내 위치"
        marker.tag = 0
        marker.mapPoint = uNowPosition
        marker.markerType = MapPOIItem.MarkerType.BluePin

        //마커를 클릭했을 때
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)

    }


    //키워드 검색 함수
    private fun searchKeyword(keyword: String, x: Double, y: Double, radius: Int) {
        Log.d("DoubletoStringTest", x.toString() + " " + y.toString())
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, keyword,  x.toString() , y.toString(), radius)

        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                addItemsAndMarkers(response.body())
                Log.d("Test1", "Raw: ${response.raw()}")
                Log.d("Test1", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("MainActivity3", "통신 실패: ${t.message}")
            }
        })
    }


    //검색 결과 처리
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?){
        if(!searchResult?.documents.isNullOrEmpty()) {
            //검색 결과가 있으면
            listItems.clear() //리스트 초기화
            //지도의 마커 모두 제거

            for(document in searchResult!!.documents) {
                //결과 추가가
                val item = ListItem(document.place_name
                                    ,document.road_address_name
                                    ,document.address_name
                                    ,document.x.toDouble()
                                    ,document.y.toDouble())

                listItems.add(item)

                //지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                            document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                mapView.addPOIItem(point)
             //   Log.d("Test", "Raw: ${response.raw()}")
                //KeyWord 검색 결과가 잘 나왔는지 Log
                //마커에 넣어야 할 부분 (x,y,group_name or category_name 에서 스트링 검색해서 if(일식, 한식) )
                //음식 종류 구분해서 일식집, 한식집, 중식집만 구분해서 나타날 수 있게
/*                Log.d("Test1", "Body: ${document.place_name}")
                Log.d("Test2", "Body: ${document.category_group_code}")
                Log.d("Test3", "Body: ${document.category_group_name}")
                Log.d("Test4", "Body: ${document.category_name}")
                Log.d("Test5", "Body: ${document.x.toDouble()}")
                Log.d("Test6", "Body: ${document.y.toDouble()}")*/
           }
           //listItems.

        } else {
            //검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
