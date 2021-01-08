package com.pepcy.nihongo.ui.query

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.ConfigDao
import com.pepcy.nihongo.database.ConfigEntity
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.database.WordsDatabaseUtil
import com.pepcy.nihongo.webliocrawler.WeblioCrawler
import com.pepcy.nihongo.word.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class QueryViewModel(
    val dao: WordsDao,
    private val configDao: ConfigDao,
    private val resources: Resources,
) : ViewModel() {
    var queryWord = MutableLiveData("")
    var wordList = MutableLiveData<ArrayList<Word>>()

    private var _navigateToWord = MutableLiveData<Boolean?>()
    val navigateToWord: LiveData<Boolean?>
        get() = _navigateToWord

    private var newestDataTime = 0L
    var selectedWord = Word()

    private var _showInitWaitingDialog = MutableLiveData<Boolean?>()
    val showInitWaitingDialog: LiveData<Boolean?>
        get() = _showInitWaitingDialog
    private var _dismissInitWaitingDialog = MutableLiveData<Boolean?>()
    val dismissInitWaitingDialog: LiveData<Boolean?>
        get() = _dismissInitWaitingDialog
    private var _dialogMessage = MutableLiveData<String?>()
    val dialogMessage: LiveData<String?>
        get() = _dialogMessage

    fun queryWord(str: String) {
        viewModelScope.launch {
            val startDataTime = System.currentTimeMillis()
            val crawler = WeblioCrawler()
            val result = withContext(Dispatchers.IO) {
                WordsDatabaseUtil.queryWordFromDatabase(dao, str)
            }
            if (startDataTime > newestDataTime) {
                wordList.value = result
            }
            val netResult = withContext(Dispatchers.IO) {
                crawler.queryWord(str)
            }

            result.addAll(netResult)
            if (startDataTime > newestDataTime) {
                newestDataTime = startDataTime
                wordList.value = result
            }
        }
    }

    fun clickWord(word: Word) {
        selectedWord = word
        _navigateToWord.value = true
    }

    fun doneNavToWordFragment() {
        _navigateToWord.value = false
    }

    private var _lang = MutableLiveData<String?>()
    val lang: LiveData<String?>
        get() = _lang

    fun loadConfig() {
        viewModelScope.launch {
            val temp = withContext(Dispatchers.IO) {
                configDao.checkConfig()
            }

            _dialogMessage.value = if (temp == 0) {
                resources.getString(R.string.init_waiting_first)
            } else {
                resources.getString(R.string.init_waiting)
            }
            _showInitWaitingDialog.value = true

            if (temp == 0) {
                withContext(Dispatchers.IO) {
                    configDao.insertConfig(ConfigEntity(1, "zh"))
                }
                _lang.value = "zh"
                withContext(Dispatchers.IO) {
                    WordsDatabaseUtil.initWords(dao, resources)
                    WordsDatabaseUtil.initBaseSet(resources)
                }
            } else {
                val entity = withContext(Dispatchers.IO) {
                    configDao.getConfig()
                }
                _lang.value = entity.lang
                withContext(Dispatchers.IO) {
                    WordsDatabaseUtil.initBaseSet(resources)
                }
            }
            _dismissInitWaitingDialog.value = true
            _showInitWaitingDialog.value = false
        }
    }

    fun downDismissWaitingDialog() {
        _dismissInitWaitingDialog.value = false
    }
}