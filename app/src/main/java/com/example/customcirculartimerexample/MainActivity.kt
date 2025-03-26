package com.example.customcirculartimerexample

import android.os.Bundle
import android.widget.Button
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
        customProgress.setIncrement(TimeUnit.SECONDS.toMillis(1))
        val markers : List<Long> = listOf(customProgress.duration / 2)
        customProgress.setMarkers(markers)

        customProgress.setCurrentValue(1000L)
        customProgress.setIncrement(10250L)
        customProgress.setFormatText(TimeFormat.HH_MM)

        btnIncrease.setOnClickListener {
            customProgress.increase()
         }

        btnDecrease.setOnClickListener {
            customProgress.decrease()
         }
    }
}