package com.pepcy.nihongo.ui.query

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pepcy.nihongo.R
import com.pepcy.nihongo.word.Word

class QueryAdapter(private val wordClickListener: WordClickListener) : RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {
    var data = listOf<Word>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class QueryViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemTitle: TextView = itemView.findViewById(R.id.text_item_title)
        private val itemKana: TextView = itemView.findViewById(R.id.text_item_kana)
        private val itemLocal: TextView = itemView.findViewById(R.id.text_item_local)
        private val itemAbstract: TextView = itemView.findViewById(R.id.text_item_abstract)

        fun bind(word: Word, wordClickListener: WordClickListener) {
            itemTitle.text = word.title
            itemKana.text = word.kana
            if (word.fromLocal) {
                itemLocal.text = itemView.context.getString(R.string.local_result)
            } else {
                itemLocal.text = ""
            }
            itemAbstract.text = word.getMeaningString(20)
            itemTitle.setOnClickListener {
                wordClickListener.onWordClickListener(word)
            }
            itemKana.setOnClickListener {
                wordClickListener.onWordClickListener(word)
            }
            itemAbstract.setOnClickListener {
                wordClickListener.onWordClickListener(word)
            }
        }

        companion object {
            fun from(parent: ViewGroup): QueryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.query_item, parent, false)
                return QueryViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        return QueryViewHolder.from(parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        holder.bind(data[position], wordClickListener)
    }
}