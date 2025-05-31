package com.example.first_project.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ScoreDataManager {

    private const val PREFS_NAME = "score_data"
    private const val KEY_SCORES = "scores"
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    val scores = mutableListOf<Score>()


    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadScores()
    }

    fun addScore(score: Score) {
        scores.add(score)
        saveScores()
    }

    fun getTopScores(): List<Score> {
        return scores.sortedByDescending { it.score }.take(10)
    }

    fun clearScores() {
        scores.clear()
        saveScores()

    }

    private fun saveScores() {
        val json = gson.toJson(scores)
        sharedPreferences.edit().putString(KEY_SCORES, json).apply()
    }

    private fun loadScores() {
        scores.clear()
        val json = sharedPreferences.getString(KEY_SCORES, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Score>>() {}.type
            val loaded = gson.fromJson<List<Score>>(json, type)
            scores.addAll(loaded)
        }
    }

    //for pre game testing
    fun preloadSampleData() {
        if (scores.isEmpty()) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            scores.add(
                Score.Builder()
                    .score(117)
                    .releaseDate(LocalDate.parse("13/12/2023", formatter))
                    .lat(32.0853)
                    .lon(34.7818)
                    .build()
            )
        }
    }
}