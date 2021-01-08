package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.learning.WordSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class SetStatViewModel(private val dao: WordsDao, private val setName: String) : ViewModel() {
    val setDayData: ArrayList<Long> = ArrayList()
    val setAccuracyData: ArrayList<Double> = ArrayList()
    val setNewCountData: ArrayList<Int> = ArrayList()
    val setReviewCountData: ArrayList<Int> = ArrayList()
    val setLearnCountData: ArrayList<Int> = ArrayList()
    val setTimeData: ArrayList<Long> = ArrayList()
    lateinit var setRatioData: Array<Double>

    private var _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    private var _initDataDown = MutableLiveData<Boolean?>()
    val initDataDown: LiveData<Boolean?>
        get() = _initDataDown

    fun initData() {
        viewModelScope.launch {
            setRatioData = withContext(Dispatchers.IO) {
                val entity = dao.getWordSet(setName)!!
                val wordSet = WordSet(dao, entity)
                wordSet.getLevelStat()
            }

            val setDailyEntities = withContext(Dispatchers.IO) {
                dao.getWordSetDaily(setName)
            }

            val limit = 30
            var count = 0
            val dayCount = WordSet.getDayCountOfToday()
            for (entity in setDailyEntities) {
                setDayData.add(entity.day - dayCount)
                setAccuracyData.add(entity.accuracy)
                setNewCountData.add(entity.newCount)
                setReviewCountData.add(entity.reviewCount)
                setLearnCountData.add(entity.newCount + entity.reviewCount)
                setTimeData.add(entity.timeUsed)

                ++count
                if (count == limit) {
                    break
                }
            }
            setDayData.reverse()
            setAccuracyData.reverse()
            setNewCountData.reverse()
            setReviewCountData.reverse()
            setLearnCountData.reverse()
            setTimeData.reverse()

            _initDataDown.value = true
        }
    }

    fun navToHome() {
        _navigateToHome.value = true
    }
    fun downNavToHome() {
        _navigateToHome.value = false
    }
}