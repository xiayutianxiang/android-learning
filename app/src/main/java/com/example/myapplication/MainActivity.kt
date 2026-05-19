package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.widget.CountdownView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val countdownView = CountdownView(this)
        setContentView(countdownView)

        ViewCompat.setOnApplyWindowInsetsListener(countdownView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val targetTime = System.currentTimeMillis() +
                TimeUnit.DAYS.toMillis(2) +
                TimeUnit.HOURS.toMillis(3) +
                TimeUnit.MINUTES.toMillis(45) +
                TimeUnit.SECONDS.toMillis(30)
        countdownView.setTargetTime(targetTime)
    }
}