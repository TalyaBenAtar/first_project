package com.example.first_project.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_project.R
import com.example.first_project.adapters.ScoreAdapter
import com.example.first_project.interfaces.Callback_HighScoreClicked
import com.example.first_project.model.Score
import com.example.first_project.model.ScoreDataManager


class HighScoresFragment : Fragment() {
    private lateinit var scoreAdapter: ScoreAdapter
    private lateinit var recyclerView: RecyclerView
    private val scores = mutableListOf<Score>()


    var highScoreItemClicked: Callback_HighScoreClicked? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_high_scores, container, false)
        findViews(view)
        initRecyclerView()
        return view
    }


    private fun findViews(view: View) {
        recyclerView = view.findViewById(R.id.main_RV_list)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        scores.clear()
        scores.addAll(ScoreDataManager.getTopScores())

        scoreAdapter = ScoreAdapter(scores)

        scoreAdapter.scoreCallback = object : Callback_HighScoreClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                highScoreItemClicked?.highScoreItemClicked(lat, lon)
            }

            override fun onClick(score: Score) {
                highScoreItemClicked?.onClick(score)
            }
        }
        recyclerView.adapter = scoreAdapter
    }
}