package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.WordsDao
import java.lang.IllegalArgumentException

class LearnHomeViewModelFactory(private val dao: WordsDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnHomeViewModel::class.java)) {
            return LearnHomeViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}