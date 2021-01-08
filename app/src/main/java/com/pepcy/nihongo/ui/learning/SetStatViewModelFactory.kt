package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.WordsDao
import java.lang.IllegalArgumentException

class SetStatViewModelFactory(private val dao: WordsDao, private val setName: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetStatViewModel::class.java)) {
            return SetStatViewModel(dao, setName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}