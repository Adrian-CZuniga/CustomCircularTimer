package com.example.customprogresstimerexample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.customprogresstimer.ProgressTimerView
import com.example.customcirculartimerexample.R
import com.example.customprogresstimer.ProgressRoundedRectTimerView
import com.example.customprogresstimer.TextTimerView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val customProgress = findViewById<ProgressTimerView>(R.id.customProgress)
        val customProgressRoundedRect = findViewById<ProgressRoundedRectTimerView>(R.id.customProgressRoundedRect)
        val customTextTimerView = findViewById<TextTimerView>(R.id.textViewTimer)

        val btnIncrease = findViewById<Button>(R.id.btnIncrease)
        val btnDecrease = findViewById<Button>(R.id.btnDecrease)
        customProgress.setRange(0L, TimeUnit.SECONDS.toMillis(120))

        customProgress.attachMarkersByDivider(10)
        customProgress.onValueReachedMarkerListener = object : ProgressTimerView.OnValueReachedMarkerListener {
            override fun onMarkerReached(marker: Long) {
                Toast.makeText(this@MainActivity, "Marker reached: $marker", Toast.LENGTH_SHORT).show()
            }
        }

        customProgressRoundedRect.setRange(0L, TimeUnit.SECONDS.toMillis(120))
        customProgressRoundedRect.setStrokeWidthTimer(30f)
        customProgressRoundedRect.attachMarkersByDivider(2)
        customProgressRoundedRect.cornerRadius = 200f


        customProgress.setOnClickListener{
            Log.d("MainActivity", "customProgress clicked")
        }

        customProgressRoundedRect.setOnClickListener{
            Log.d("MainActivity", "customProgressRoundedRect clicked")
        }
        btnIncrease.setOnClickListener {
            customProgress.increase()
            customProgressRoundedRect.increase()
            customTextTimerView.increase()

        }

        btnDecrease.setOnClickListener {
            customProgress.decrease()
            customProgressRoundedRect.decrease()
            customTextTimerView.decrease()
        }
    }
}