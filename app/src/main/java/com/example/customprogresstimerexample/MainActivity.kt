package com.example.customprogresstimerexample

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.customprogresstimer.ProgressTimerView
import com.example.customcirculartimerexample.R
import com.example.customprogresstimer.ProgressRoundedRectTimerView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val customProgress = findViewById<ProgressTimerView>(R.id.customProgress)
        val customProgressRoundedRect = findViewById<ProgressRoundedRectTimerView>(R.id.customProgressRoundedRect)

        val btnIncrease = findViewById<Button>(R.id.btnIncrease)
        val btnDecrease = findViewById<Button>(R.id.btnDecrease)
        customProgress.setRange(0L, TimeUnit.SECONDS.toMillis(120))
        customProgress.setStrokeWidthTimer(50f)

        customProgress.setProgressColor(Color.argb(255, 255, 0, 0))
        customProgress.setMarkerColor(Color.argb(255, 0, 255, 0))
        customProgress.setBackgroundTimerColor(Color.argb(255, 0, 0, 255))
        customProgress.setMarkerSecondaryColor(Color.argb(255, 255, 255, 0))
        customProgress.attachMarkersByDivider(10)
        customProgress.onValueReachedMarkerListener = object : ProgressTimerView.OnValueReachedMarkerListener {
            override fun onMarkerReached(marker: Long) {
                Toast.makeText(this@MainActivity, "Marker reached: $marker", Toast.LENGTH_SHORT).show()
            }
        }

        customProgressRoundedRect.setRange(0L, TimeUnit.SECONDS.toMillis(120))
        customProgressRoundedRect.setStrokeWidthTimer(30f)
        customProgressRoundedRect.attachMarkersByDivider(2)
        customProgressRoundedRect.setCornerRadius(20f)

        btnIncrease.setOnClickListener {
            customProgress.increase()
            customProgressRoundedRect.increase()
        }

        btnDecrease.setOnClickListener {
            customProgress.decrease()
            customProgressRoundedRect.decrease()
        }
    }
}