package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ResultViewModelFactory(private val timeDiff: Long, private val accuracy: Float) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(timeDiff, accuracy) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}