package com.example.first_project.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.first_project.interfaces.TiltCallback
import kotlin.math.abs

class TiltDetector(context: Context, private var tiltCallback: TiltCallback?) {

    private val sensorManager = context
        .getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensor = sensorManager
        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor

    private lateinit var sensorEventListener: SensorEventListener

    var tiltCounterX: Int = 0
        private set

    var tiltCounterY: Int = 0
        private set

    var currentX: Float = 0f
        private set

    var currentY: Float = 0f
        private set

    private var canTiltRight = true
    private var canTiltLeft = true


    private var timestamp: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                calculateTilt(x, y)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // pass
            }
        }
    }

    private fun calculateTilt(x: Float, y: Float) {
        val threshold = 3.0
        if (System.currentTimeMillis() - timestamp >= 500) {
            timestamp = System.currentTimeMillis()
            if (x < -threshold) {
                tiltCounterX++
                tiltCallback?.tiltRight()
            } else if (x> threshold){
                tiltCounterX--
                tiltCallback?.tiltLeft()
            }

            if (y < -threshold) {
                tiltCounterY++
                tiltCallback?.tiltFront()
            } else if (y> threshold) {
                tiltCounterY--
                tiltCallback?.tiltBack()
            }
        }
    }


    fun start() {
        sensorManager
            .registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
    }

    fun stop() {
        sensorManager
            .unregisterListener(
                sensorEventListener,
                sensor
            )
    }
}