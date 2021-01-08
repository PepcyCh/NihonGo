package com.pepcy.nihongo.ui.query

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pepcy.nihongo.R
import com.pepcy.nihongo.word.Word
import com.pepcy.nihongo.word.WordItem
import com.pepcy.nihongo.word.WordType

class WordAdapter(private val word: Word) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    class WordViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val itemType = itemView.findViewById<TextView>(R.id.word_item_type)
        private val itemMeanings = itemView.findViewById<LinearLayout>(R.id.word_item_meanings)

        fun bind(wordItem: WordItem, suru: Boolean) {
            val typeface = itemView.resources.getFont(R.font.sourcehansans_jp_normal)

            var firstMeaning = true
            if (wordItem.type != WordType.NONE) {
                if (suru) {
                    itemType.text = wordItem.type.toString() + "（スル）"
                } else {
                    itemType.text = wordItem.type.toString()
                }
            } else {
                itemType.visibility = View.GONE;
            }
            for (meaning in wordItem.meanings) {
                if (wordItem.type != WordType.NONE || !firstMeaning) {
                    val separatorView = View(itemMeanings.context)
                    separatorView.layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
                    separatorView.setBackgroundColor(Color.BLACK)
                    itemMeanings.addView(separatorView)
                }

                val meaningText = TextView(itemMeanings.context)
                meaningText.text = meaning.meaning
                meaningText.textSize = 18.0f
                meaningText.typeface = typeface
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 10, 0, 20)
                meaningText.layoutParams = params
                itemMeanings.addView(meaningText)

                for (example in meaning.examples) {
                    val exampleText = TextView(itemMeanings.context)
                    exampleText.text = example
                    exampleText.setTextColor(Color.parseColor("#AAAAAA"))
                    exampleText.textSize = 14.0f
                    exampleText.typeface = typeface
                    itemMeanings.addView(exampleText)
                }

                firstMeaning = false
            }
            for (extra in wordItem.extra) {
                if (wordItem.type != WordType.NONE || !firstMeaning) {
                    val separatorView = View(itemMeanings.context)
                    separatorView.layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
                    separatorView.setBackgroundColor(Color.BLACK)
                    itemMeanings.addView(separatorView)
                }

                val keyText = TextView(itemMeanings.context)
                keyText.text = extra.key
                keyText.textSize = 18.0f
                keyText.typeface = typeface
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 10, 0, 20)
                keyText.layoutParams = params
                keyText.setTextColor(Color.parseColor("#6666AA"))
                itemMeanings.addView(keyText)

                val valueText = TextView(itemMeanings.context)
                valueText.text = extra.value
                valueText.setTextColor(Color.parseColor("#AAAAAA"))
                valueText.textSize = 14.0f
                valueText.typeface = typeface
                itemMeanings.addView(valueText)

                firstMeaning = false
            }
        }

        companion object {
            fun from(parent: ViewGroup): WordViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.word_item, parent, false)
                return WordViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder.from(parent)
    }

    override fun getItemCount(): Int = word.items.size

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(word.items[position], word.suru)
    }
}