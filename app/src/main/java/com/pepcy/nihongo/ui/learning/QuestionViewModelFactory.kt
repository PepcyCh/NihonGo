package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.learning.Task
import java.lang.IllegalArgumentException

class QuestionViewModelFactory(private val dao: WordsDao, private val task: Task) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            return QuestionViewModel(dao, task) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}