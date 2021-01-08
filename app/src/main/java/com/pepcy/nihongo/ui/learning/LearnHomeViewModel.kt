package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.database.WordsDatabaseUtil
import com.pepcy.nihongo.learning.WordSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LearnHomeViewModel(val dao: WordsDao) : ViewModel() {
    var wordSets = MutableLiveData<ArrayList<WordSet>>()
    var selectedWordSet = WordSet("__NULL__", dao)

    private var _navigateToQuestion = MutableLiveData<Boolean?>()
    val navigateToQuestion: LiveData<Boolean?>
        get() = _navigateToQuestion

    private var _navigateToSetting = MutableLiveData<Boolean?>()
    val navigateToSetting: LiveData<Boolean?>
        get() = _navigateToSetting

    private var _navigateToStat = MutableLiveData<Boolean?>()
    val navigateToStat: LiveData<Boolean?>
        get() = _navigateToStat

    private var _navigateToNew = MutableLiveData<Boolean?>()
    val navigateToNew: LiveData<Boolean?>
        get() = _navigateToNew

    fun getAllWordSets() {
        viewModelScope.launch {
            wordSets.value = withContext(Dispatchers.IO) {
                WordsDatabaseUtil.getAllWordSets(dao)
            }
        }
    }

    fun newSet() {
        _navigateToNew.value = true
    }
    fun downNavToNew() {
        _navigateToNew.value = false
    }

    fun startTaskFromWordSet(wordSet: WordSet) {
        selectedWordSet = wordSet
        _navigateToQuestion.value = true
    }
    fun downNavToQuestion() {
        _navigateToQuestion.value = false
    }

    fun settingWordSet(wordSet: WordSet) {
        selectedWordSet = wordSet
        _navigateToSetting.value = true
    }
    fun downNavToSetting() {
        _navigateToSetting.value = false
    }

    fun showWordSetStat(wordSet: WordSet) {
        selectedWordSet = wordSet
        _navigateToStat.value = true
    }
    fun downNavToStat() {
        _navigateToStat.value = false
    }
}