package com.example.first_project

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.first_project.interfaces.Callback_HighScoreClicked
import com.example.first_project.model.Score
import com.example.first_project.model.ScoreDataManager
import com.example.first_project.ui.HighScoresFragment
import com.example.first_project.ui.MapFragment
import com.google.android.material.button.MaterialButton

class ScoreBoardActivity : AppCompatActivity() {

    private lateinit var game_BTN_DeleteData: MaterialButton
    private lateinit var game_BTN_Menu: MaterialButton
    private lateinit var main_FRAME_highScores: FrameLayout
    private lateinit var main_FRAME_map: FrameLayout
    private lateinit var mapFragment: MapFragment
    private lateinit var highScoresFragment: HighScoresFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScoreDataManager.init(applicationContext)

        enableEdgeToEdge()
        setContentView(R.layout.activity_scores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        initViews()
    }

    private fun findViews() {
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
        main_FRAME_highScores = findViewById(R.id.main_FRAME_highScores)
        game_BTN_Menu= findViewById(R.id.game_BTN_Menu)
        game_BTN_DeleteData= findViewById(R.id.game_BTN_DeleteData)
    }

    private fun initViews() {
        game_BTN_Menu.setOnClickListener {
            val intent = Intent(this, GameMenuActivity::class.java)
            startActivity(intent)
        }

        game_BTN_DeleteData.setOnClickListener {
            ScoreDataManager.clearScores()
        }

        mapFragment = MapFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_map, mapFragment)
            .commit()

        highScoresFragment = HighScoresFragment()
        highScoresFragment.highScoreItemClicked =
            object : Callback_HighScoreClicked {
                override fun highScoreItemClicked(lat: Double, lon: Double) {
                    mapFragment.zoom(lat, lon) // optional
                }

                override fun onClick(score: Score) {
                    mapFragment.zoom(score.lat, score.lon)
                }
            }
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_highScores, highScoresFragment)
            .commit()
    }
}
