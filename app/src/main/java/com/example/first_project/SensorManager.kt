package com.example.first_project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.example.first_project.interfaces.TiltCallback
//import com.example.first_project.utilities.BackgroundMusicPlayer
//import com.example.first_project.utilities.ImageLoader
import com.example.first_project.utilities.SingleSoundPlayer
import com.example.first_project.utilities.TiltDetector

class SensorManager : AppCompatActivity() {

    private lateinit var sensors_LBL_tiltX: MaterialTextView
    private lateinit var sensors_LBL_tiltY: MaterialTextView
    private lateinit var tiltDetector: TiltDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        initTiltDetector()
    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback{
                override fun tiltFront() {
                    tiltDetector.tiltCounterY.toString().also {
                    }
                }

                override fun tiltBack() {
                    tiltDetector.tiltCounterY.toString().also {
                    }
                }

                override fun tiltRight() {
                    tiltDetector.tiltCounterX.toString().also {
                    }
                }

                override fun tiltLeft() {
                    tiltDetector.tiltCounterX.toString().also {
                    }
                }
            }
        )
    }


    private fun findViews() {
        sensors_LBL_tiltX = findViewById(R.id.sensors_LBL_tiltX)
        sensors_LBL_tiltY = findViewById(R.id.sensors_LBL_tiltY)
    }

    override fun onResume() {
        super.onResume()
        tiltDetector.start()
    }

    override fun onPause() {
        super.onPause()
        tiltDetector.stop()
    }
}