package com.example.first_project.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.first_project.interfaces.Callback_HighScoreClicked
import com.example.first_project.model.Score
import java.time.format.DateTimeFormatter
import com.example.first_project.databinding.ScoreItemBinding


class ScoreAdapter(
    private val scores: List<Score>
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {
    var scoreCallback: Callback_HighScoreClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ScoreItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ScoreViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return scores.size
    }

    private fun getItem(position: Int) = scores[position]

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.bind(score, position)
    }


    inner class ScoreViewHolder(private val binding: ScoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(score: Score, position: Int) {
            // Set placement number (starts at 1)
            binding.gameLBLPlacement.text = (position + 1).toString()

            // Set score value
            binding.gameLBLScore.text = "Score: ${score.score}"

            // Format date
            val formattedDate = score.releaseDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
            binding.gameLBLPlayedDate.text = formattedDate

            // Click entire card
            binding.scoreCVData.setOnClickListener {
                scoreCallback?.onClick(score)
            }
        }
    }
}
