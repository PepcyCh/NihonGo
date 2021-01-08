package com.pepcy.nihongo.ui.query

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.word.Word
import java.lang.IllegalArgumentException

class WordViewModelFactory(private val dao: WordsDao, private val word: Word) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            return WordViewModel(dao, word) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}