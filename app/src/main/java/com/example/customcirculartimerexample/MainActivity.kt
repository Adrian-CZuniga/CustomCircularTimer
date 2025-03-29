package com.example.customcirculartimerexample

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.customcirculartimer.CircularTimerView
import com.example.customcirculartimer.TimeFormat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val customProgress = findViewById<CircularTimerView>(R.id.customProgress)
        val btnIncrease = findViewById<Button>(R.id.btnIncrease)
        val btnDecrease = findViewById<Button>(R.id.btnDecrease)
        customProgress.setRange(0L, TimeUnit.SECONDS.toMillis(120))
        val markers : List<Long> = listOf(customProgress.duration / 2)
        customProgress.setStrokeWidthTimer(50f)
        customProgress.setIncrement()
        customProgress.setFormatText(TimeFormat.HH_MM)

        customProgress.setProgressColor(Color.argb(255, 255, 0, 0))
        customProgress.setMarkerColor(Color.argb(255, 0, 255, 0))
        customProgress.setBackgroundTimerColor(Color.argb(255, 0, 0, 255))
        customProgress.setMarkerSecondaryColor(Color.argb(255, 255, 255, 0))
        customProgress.setMarkersByDivider(10)
        customProgress.onValueReachedMarkerListener = object : CircularTimerView.OnValueReachedMarkerListener {
            override fun onMarkerReached(marker: Long) {
                Toast.makeText(this@MainActivity, "Marker reached: $marker", Toast.LENGTH_SHORT).show()
            }
        }

        btnIncrease.setOnClickListener {
            customProgress.increase()
         }

        btnDecrease.setOnClickListener {
            customProgress.decrease()
         }
    }
}