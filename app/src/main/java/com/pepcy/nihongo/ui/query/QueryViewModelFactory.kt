package com.pepcy.nihongo.ui.query

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.ConfigDao
import com.pepcy.nihongo.database.WordsDao
import java.lang.IllegalArgumentException

class QueryViewModelFactory(
    private val dao: WordsDao,
    private val configDao: ConfigDao,
    private val resources: Resources,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QueryViewModel::class.java)) {
            return QueryViewModel(dao, configDao, resources) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}