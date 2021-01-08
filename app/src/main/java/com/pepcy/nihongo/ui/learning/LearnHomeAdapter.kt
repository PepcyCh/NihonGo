package com.pepcy.nihongo.ui.learning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pepcy.nihongo.R
import com.pepcy.nihongo.learning.WordSet

class LearnHomeAdapter(
    private val wordSetClickListener: WordSetClickListener
) : RecyclerView.Adapter<LearnHomeAdapter.LearnHomeViewHolder>() {
    var wordSets = listOf<WordSet>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class LearnHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val setName: TextView = itemView.findViewById(R.id.learn_word_set_name)
        private val totalCount: TextView = itemView.findViewById(R.id.learn_word_set_total)
        private val reviewCount: TextView = itemView.findViewById(R.id.learn_word_set_review)
        private val newCount: TextView = itemView.findViewById(R.id.learn_word_set_new)
        private val startBtn: Button = itemView.findViewById(R.id.learn_word_set_start)
        private val settingBtn: Button = itemView.findViewById(R.id.learn_word_set_setting)
        private val statBtn: Button = itemView.findViewById(R.id.learn_word_set_stat)

        fun bind(set: WordSet, wordSetClickListener: WordSetClickListener) {
            setName.text = set.setName
            totalCount.text = set.cardsCount.toString()
            reviewCount.text = set.reviewWords.toString()
            newCount.text = set.newWordsToday.toString()

            startBtn.isClickable = set.needToReview
            startBtn.setOnClickListener {
                wordSetClickListener.onStartClick(set)
            }
            settingBtn.setOnClickListener {
                wordSetClickListener.onSettingClick(set)
            }
            statBtn.setOnClickListener {
                wordSetClickListener.onStatClick(set)
            }
        }

        companion object {
            fun from(parent: ViewGroup): LearnHomeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.learn_home_item, parent, false)
                return LearnHomeViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnHomeViewHolder {
        return LearnHomeViewHolder.from(parent)
    }

    override fun getItemCount(): Int = wordSets.size

    override fun onBindViewHolder(holder: LearnHomeViewHolder, position: Int) {
        holder.bind(wordSets[position], wordSetClickListener)
    }
}