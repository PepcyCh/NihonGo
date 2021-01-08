package com.pepcy.nihongo.ui.learning

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.WordSetEntity
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.learning.WordSet
import java.lang.IllegalArgumentException

class SetSettingViewModelFactory(
        private val dao: WordsDao,
        private val wordSetEntity: WordSetEntity,
        private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetSettingViewModel::class.java)) {
            return SetSettingViewModel(dao, wordSetEntity, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}