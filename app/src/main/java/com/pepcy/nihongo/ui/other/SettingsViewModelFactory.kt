package com.pepcy.nihongo.ui.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pepcy.nihongo.database.ConfigDao
import java.lang.IllegalArgumentException

class SettingsViewModelFactory(private val dao: ConfigDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}