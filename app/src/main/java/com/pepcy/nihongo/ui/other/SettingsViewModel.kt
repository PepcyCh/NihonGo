package com.pepcy.nihongo.ui.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepcy.nihongo.database.ConfigDao
import com.pepcy.nihongo.database.ConfigEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val dao: ConfigDao) : ViewModel() {
    val langName = MutableLiveData("zh")
    val langNameShow = MutableLiveData("中文")

    private var _navigateToOther = MutableLiveData<Boolean?>()
    val navigateToOther: LiveData<Boolean?>
        get() = _navigateToOther

    fun navToOther() {
        _navigateToOther.value = true
    }
    fun downNavToOther() {
        _navigateToOther.value = false
    }

    fun getConfig() {
        viewModelScope.launch {
            val entity = withContext(Dispatchers.IO) {
                dao.getConfig()
            }
            if (entity.lang == "zh") {
                langName.value = "zh"
                langNameShow.value = "中文"
            } else if (entity.lang == "en") {
                langName.value = "en"
                langNameShow.value = "English"
            }
        }
    }

    fun saveConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateConfig(ConfigEntity(1, langName.value!!))
        }
    }
}