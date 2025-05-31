package com.example.first_project.interfaces

import com.example.first_project.model.Score

interface Callback_HighScoreClicked {
    fun onClick(score: Score)
    fun highScoreItemClicked(lat: Double, lon: Double)
}