package com.pepcy.nihongo.ui.learning

import com.pepcy.nihongo.learning.WordSet

interface WordSetClickListener {
    fun onStartClick(wordSet: WordSet)
    fun onSettingClick(wordSet: WordSet)
    fun onStatClick(wordSet: WordSet)
}