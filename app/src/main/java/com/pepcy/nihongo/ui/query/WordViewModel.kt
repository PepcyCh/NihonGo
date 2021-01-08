package com.pepcy.nihongo.ui.query

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.database.WordsDatabaseUtil
import com.pepcy.nihongo.word.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordViewModel(private val dao: WordsDao, var word: Word) : ViewModel() {
    var wordSetNames = ArrayList<String>()

    private var _namesReady = MutableLiveData<Boolean?>()
    val namesReady: LiveData<Boolean?>
        get() = _namesReady

    private var addReady = true

    private var _dupCard = MutableLiveData<Boolean?>()
    val dupCard: LiveData<Boolean?>
        get() = _dupCard

    fun fetchWordSetNames() {
        if (!addReady) {
            return
        }

        addReady = false
        _dupCard.value = false
        viewModelScope.launch {
            _namesReady.value = false
            wordSetNames = withContext(Dispatchers.IO) {
                WordsDatabaseUtil.getAllWordSetNames(dao)
            }
            _namesReady.value = true
        }
    }

    fun addToSet(setName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (WordsDatabaseUtil.checkCardInSet(dao, setName, word)) {
                withContext(Dispatchers.Main) {
                    _dupCard.value = true
                }
            } else {
                WordsDatabaseUtil.insertCardToSet(dao, setName, word)
                addReady = true
            }
        }
    }
}