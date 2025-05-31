package com.example.first_project

import android.content.Intent
import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textview.MaterialTextView
import com.example.first_project.utilities.Constants
import com.example.first_project.utilities.SignalManager
import com.google.android.material.button.MaterialButton

class GameMenuActivity : AppCompatActivity() {

    private lateinit var score_LBL_status: MaterialTextView
    private lateinit var game_BTN_start: MaterialButton
    private lateinit var game_BTN_score_board: MaterialButton
    private lateinit var game_BTN_button_sensor: ToggleButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        initViews()
    }

    private fun initViews() {
        val bundle = intent.extras
        val message = bundle?.getString(Constants.BundleKeys.MESSAGE_KEY) ?: "LET'S PLAY!"
        val score = bundle?.getInt(Constants.BundleKeys.SCORE_KEY, -1) ?: -1

        score_LBL_status.text = if (score >= 0) {
            "$message\n$score"
        } else {
            message
        }

        game_BTN_score_board.setOnClickListener {
            val intent = Intent(this, ScoreBoardActivity::class.java)
            startActivity(intent)
        }

        game_BTN_start.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USE_SENSOR_MODE", game_BTN_button_sensor.isChecked)
            startActivity(intent)
        }

        game_BTN_button_sensor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Sensor mode enabled
                SignalManager.getInstance().toast("Sensor Mode Enabled")
            } else {
                // Button mode enabled
                SignalManager.getInstance().toast("Button Mode Enabled")
            }
        }
    }

    private fun findViews() {
        score_LBL_status = findViewById(R.id.score_LBL_status)
        game_BTN_score_board= findViewById(R.id.game_BTN_scores)
        game_BTN_start = findViewById(R.id.game_BTN_start)
        game_BTN_button_sensor= findViewById(R.id.game_BTN_sensor_button)
    }
}