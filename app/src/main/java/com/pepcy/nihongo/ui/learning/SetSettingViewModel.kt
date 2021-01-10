package com.pepcy.nihongo.ui.learning

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepcy.nihongo.R
import com.pepcy.nihongo.baseset.BaseSet
import com.pepcy.nihongo.database.WordSetEntity
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.database.WordsDatabaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SetSettingViewModel(
        private val dao: WordsDao,
        private var wordSetEntity: WordSetEntity,
        private val context: Context
) : ViewModel() {
    var setName = wordSetEntity.setName
    var newWordsPerDay = wordSetEntity.newWordsPerDay
    var questionsPerCard = wordSetEntity.questionsPerCard
    var questionSingleMeaning = wordSetEntity.questionSingleMeaning

    val addSetName = MutableLiveData<String?>()
    private var _insertCount = MutableLiveData(-1)
    val insertCount: LiveData<Int>
        get() = _insertCount
    private var _insertCountMax = MutableLiveData(0)
    val insertCountMax: LiveData<Int>
        get() = _insertCountMax

    private var _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    private var _isSaving = MutableLiveData<Boolean?>()
    val isSaving: LiveData<Boolean?>
        get() = _isSaving

    private var _toastInfo = MutableLiveData<String?>()
    val toastInfo: LiveData<String?>
        get() = _toastInfo

    fun saveWordSet() {
        if (setName.isEmpty()) {
            _toastInfo.value = context.getString(R.string.new_set_empty)
            return
        }
        if (newWordsPerDay !in 1..100) {
            _toastInfo.value = context.getString(R.string.new_set_new)
            return
        }
        if (questionsPerCard !in 1..4) {
            _toastInfo.value = context.getString(R.string.new_set_ques)
            return
        }

        _toastInfo.value = null
        _isSaving.value = true

        viewModelScope.launch {
            wordSetEntity = WordSetEntity(
                setName,
                newWordsPerDay,
                questionsPerCard,
                questionSingleMeaning,
                wordSetEntity.latestLearnDay,
                wordSetEntity.createTime,
            )
            withContext(Dispatchers.IO) {
                dao.updateWordSet(wordSetEntity)
            }
            _navigateToHome.value = true
        }
    }

    fun deleteWordSet() {
        _isSaving.value = true
        viewModelScope.launch {
            wordSetEntity = WordSetEntity(
                setName,
                newWordsPerDay,
                questionsPerCard,
                questionSingleMeaning,
                wordSetEntity.latestLearnDay,
                wordSetEntity.createTime,
            )
            withContext(Dispatchers.IO) {
                dao.deleteWordSet(wordSetEntity)
                dao.deleteCardsInWordSet(wordSetEntity.setName)
                dao.deleteDailyOfWordSet(wordSetEntity.setName)
            }
            _navigateToHome.value = true
        }
    }

    fun addSet() {
        viewModelScope.launch {
            val baseSet = BaseSet.getSet(addSetName.value!!)
            _insertCountMax.value = baseSet.size
            if (_insertCountMax.value == 0) {
                return@launch
            }

            _insertCount.value = 0
            for ((i, word) in baseSet.withIndex()) {
                withContext(Dispatchers.IO) {
                    WordsDatabaseUtil.insertCardToSet(dao, wordSetEntity.setName, word)
                }
                _insertCount.value = i + 1
            }
            addSetName.value = null
        }
    }

    fun downNavToHome() {
        _navigateToHome.value = false
    }
}