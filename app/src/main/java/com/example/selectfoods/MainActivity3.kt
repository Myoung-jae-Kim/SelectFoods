package com.example.selectfoods

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPoint

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val mapView = MapView(this)

        val mapViewContainer = findViewById<ConstraintLayout>(R.id.map_view) as ViewGroup
        mapViewContainer.addView(mapView)

    }
}