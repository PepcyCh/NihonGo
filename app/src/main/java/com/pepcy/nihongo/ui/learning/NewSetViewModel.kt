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
import com.pepcy.nihongo.word.WordLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewSetViewModel(private val dao: WordsDao, private val context: Context) : ViewModel() {
    var setName = ""
    var newWordsPerDay = 0
    var questionsPerCard = 0
    var questionSingleMeaning = false

    private var _toastInfo = MutableLiveData<String?>()
    val toastInfo: LiveData<String?>
        get() = _toastInfo
    private var _dupInfo = MutableLiveData<String?>()
    val dupInfo: LiveData<String?>
        get() = _dupInfo

    private var _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    private var isCreating = false
    private var isDuplicated = false
    val canCreate: Boolean
        get() = !isCreating && !isDuplicated

    private var _insertCount = MutableLiveData(-1)
    val insertCount: LiveData<Int>
        get() = _insertCount
    private var _insertCountMax = MutableLiveData(0)
    val insertCountMax: LiveData<Int>
        get() = _insertCountMax

    val baseSetName = MutableLiveData("empty")
    val baseSetShowName = MutableLiveData(context.resources.getStringArray(R.array.base_set_names)[0])

    fun createWordSet() {
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

        isCreating = true
        _toastInfo.value = null

        val wordSetEntity = WordSetEntity(setName, newWordsPerDay, questionsPerCard,
                questionSingleMeaning, 0, System.currentTimeMillis())
        addWordsFromBaseSet(wordSetEntity, BaseSet.getSet(baseSetName.value!!))
    }

    fun downNavToHome() {
        _navigateToHome.value = false
    }

    fun queryDuplicatedName(name: String) {
        viewModelScope.launch {
            val res = withContext(Dispatchers.IO) {
                dao.hasWordSetWithName(name) != 0
            }
            if (res) {
                isDuplicated = true
                _dupInfo.value = context.getString(R.string.new_set_dup, name)
            } else {
                isDuplicated = false
                _dupInfo.value = ""
            }
        }
    }

    private fun addWordsFromBaseSet(wordSetEntity: WordSetEntity, baseSet: ArrayList<WordLabel>) {
        viewModelScope.launch {
            val dup = withContext(Dispatchers.IO) {
                dao.hasWordSetWithName(wordSetEntity.setName) != 0
            }
            if (dup) {
                isCreating = false
                return@launch
            }

            withContext(Dispatchers.IO) {
                dao.insertWordSet(wordSetEntity)
            }
            _insertCountMax.value = baseSet.size
            if (_insertCountMax.value == 0) {
                _navigateToHome.value = true
                return@launch
            }

            _insertCount.value = 0
            for ((i, word) in baseSet.withIndex()) {
                withContext(Dispatchers.IO) {
                    WordsDatabaseUtil.insertCardToSet(dao, wordSetEntity.setName, word)
                }
                _insertCount.value = i + 1
            }
            _navigateToHome.value = true
        }
    }
}