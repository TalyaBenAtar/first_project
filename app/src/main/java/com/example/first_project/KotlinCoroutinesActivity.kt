package com.example.first_project

import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.first_project.utilities.Constants
import kotlinx.coroutines.*

class KotlinCoroutinesActivity(
    private val mainActivity: MainActivity,
    private val childrenImages: Array<ImageView>) {
    private var timerJob: Job? = null
    private var timerOn: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun startTimer() {
        if (!timerOn) {
            timerOn = true
            timerJob = coroutineScope.launch {
                var counter = 0
                while (timerOn) {
                    if (counter % 3 == 0) {
                        showRandomChild()
                    }
                    delay(1000)
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
        // Pick random child from the first row (row 0)
        val randomLane = (0 until Constants.GameLogic.LANE_NUMBER).random()
        val index = 0 * Constants.GameLogic.LANE_NUMBER + randomLane // Row 0
        childrenImages[index].visibility=View.VISIBLE
    }


}