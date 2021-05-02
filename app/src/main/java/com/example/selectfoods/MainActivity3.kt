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
//import com.example.mechacat.databinding.ActivityMainBinding
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPoint
import java.lang.NullPointerException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


//카카오 카테고리 코드 REST API
/*class KakaoApi {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK b50fe2e41bb5f4ff3197aa3c62c0c67f"
    }
}

interface KakaoApiService {
    @GET("/v2/local/search/category.json")
    //현재 내 위치 주변 음식점 가져오려면 y & x & 600, FD6
    fun getKakaoAddress(
            @Header("Authorization") key: String,
            @Query("query") address: String
    ): Call<KakaoData>
}

object KakaoApiRetrofitClient {
    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
                .baseUrl(KakaoApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
    }

    val apiService: KakaoApiService by lazy {
        retrofit
                .build()
                .create(KakaoApiService::class.java)
    }
}*/

class MainActivity3 : AppCompatActivity() {
   // private lateinit var binding: ActivityMainBinding

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK b50fe2e41bb5f4ff3197aa3c62c0c67f" // Rest api 키
    }

    private val listItems = arrayListOf<ListItem>() //검색
    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationListener? = null
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMain
        setContentView(R.layout.activity_main3)

        searchKeyword("식당")

        var toggleButton = findViewById(R.id.toggle1) as ToggleButton
        val mapView = MapView(this)
        val marker = MapPOIItem()

        val mapViewContainer = findViewById<ConstraintLayout>(R.id.map_view) as ViewGroup
        mapViewContainer.addView(mapView)

        //callKakaoRestApi("카카오")
        //버튼 클릭하면 현재 위치로

        val button1 = findViewById<Button>(R.id.toggle2)
        button1.setOnClickListener {
            searchKeyword("식당")
        }

        toggleButton.setOnClickListener {
           // searchKeyword("식당")
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val lm : LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
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

    //키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder() // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val  api = retrofit.create(KakaoAPI::class.java) //통신 인터페이스를 객체로 생성
        val  call = api.getSearchKeyword(API_KEY, keyword) //검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                //통신 성공 (검색 결과는 response.body() 에 있음)
                addItemsAndMarkers(response.body())
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                //통신 실패
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
     //                   .mapView.addPOIItem(point)
             //   Log.d("Test", "Raw: ${response.raw()}")
                //KeyWord 검색 결과가 잘 나왔는지 Log
                //마커에 넣어야 할 부분 (x,y,group_name or category_name 에서 스트링 검색해서 if(일식, 한식) )
                //음식 종류 구분해서 일식집, 한식집, 중식집만 구분해서 나타날 수 있게
                Log.d("Test1", "Body: ${document.place_name}")
                Log.d("Test2", "Body: ${document.category_group_code}")
                Log.d("Test3", "Body: ${document.category_group_name}")
                Log.d("Test4", "Body: ${document.category_name}")
                Log.d("Test5", "Body: ${document.x.toDouble()}")
                Log.d("Test6", "Body: ${document.y.toDouble()}")
           }
                    //listAdapter.noti

        } else {
            //검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}