package com.pepcy.nihongo.ui.learning

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.WordsDao
import java.lang.IllegalArgumentException

class NewSetViewModelFactory(private val dao: WordsDao, private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewSetViewModel::class.java)) {
            return NewSetViewModel(dao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}