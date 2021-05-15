package com.example.selectfoods

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
//import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.choice)
        button.setOnClickListener {
            val intent = Intent(this, SelectFoodsActivity::class.java)
            startActivity(intent)
        }

    }
}




