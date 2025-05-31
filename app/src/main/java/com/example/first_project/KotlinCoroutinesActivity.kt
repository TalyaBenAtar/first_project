package com.example.first_project

import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.first_project.utilities.Constants
import kotlinx.coroutines.*

class KotlinCoroutinesActivity(
    private val mainActivity: MainActivity,
    private val obstaclesImages: Array<ImageView>) {
    private var timerJob: Job? = null
    private var timerOn: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private var drivingSpeed: Long = 1000L

        fun setDrivingSpeed(speed: Long) {
            drivingSpeed = speed
        }
        fun getDrivingSpeed(): Long {
            return drivingSpeed
        }
    }


    fun startTimer() {
        if (!timerOn) {
            timerOn = true
            timerJob = coroutineScope.launch {
                var counter = 0
                while (timerOn) {
                    if (counter % 2 == 0) {
                        showRandomChild()
                    }
                    delay(drivingSpeed)
                    mainActivity.checkAnswer()
                    counter++
                    Log.d(
                        "Timer Runnable",
                        "Counter: $counter, Time: ${System.currentTimeMillis()}"
                    )
                }
            }
        }
    }

    fun stopTimer() {
        timerOn = false
        timerJob?.cancel()
    }

    private fun showRandomChild() {
        // Pick random spot from the first row (row 0)
        val randomLane = (0 until Constants.GameLogic.LANE_NUMBER).random()
        val index = 0 * Constants.GameLogic.LANE_NUMBER + randomLane // Row 0

        //randomize child or coin
        if (randomLane%2==0){ //child has a slightly higher chance of getting picked which is good
            obstaclesImages[index].setImageResource(R.drawable.child_target)
            obstaclesImages[index].setTag("child")
        } else {
            obstaclesImages[index].setImageResource(R.drawable.coin)
            obstaclesImages[index].setTag("coin")
            obstaclesImages[index].scaleType = ImageView.ScaleType.FIT_CENTER
        }

        obstaclesImages[index].visibility=View.VISIBLE
    }
}