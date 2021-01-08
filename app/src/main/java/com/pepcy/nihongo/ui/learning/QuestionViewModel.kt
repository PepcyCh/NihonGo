package com.pepcy.nihongo.ui.learning

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.learning.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionViewModel(private val dao: WordsDao, private val task: Task) : ViewModel() {
    val currQuestion = MutableLiveData(task.questions.first())
    private val startTime = System.currentTimeMillis()
    val timeUsed: Long
        get() = task.timeUsed

    private var _answered = MutableLiveData<Boolean?>()
    val answered: LiveData<Boolean?>
        get() = _answered

    private var currIndex = 0
    private var correctCount = 0
    val accuracy: Float
        get() = correctCount.toFloat() / task.questions.size

    private var _showWrongHint = arrayOf(
            MutableLiveData<Boolean?>(),
            MutableLiveData<Boolean?>(),
            MutableLiveData<Boolean?>(),
            MutableLiveData<Boolean?>())
    fun showWrongHint(index: Int) = _showWrongHint[index]
    private var _showCorrectHint = arrayOf(
            MutableLiveData<Boolean?>(),
            MutableLiveData<Boolean?>(),
            MutableLiveData<Boolean?>(),
            MutableLiveData<Boolean?>())
    fun showCorrectHint(index: Int) = _showCorrectHint[index]

    private var _navigateToResult = MutableLiveData<Boolean?>()
    val navigateToResult: LiveData<Boolean?>
        get() = _navigateToResult

    fun clickItem(index: Int) {
        _answered.value = true
        task.questions[currIndex].chosenItemIndex = index
        if (!task.questions[currIndex].correctlyAnswered) {
            _showWrongHint[index].value = true
        } else {
            ++correctCount
        }
        _showCorrectHint[task.questions[currIndex].correctItemIndex].value = true

        Handler(Looper.myLooper()!!).postDelayed({
            toNextQuestion(index)
        }, 500)
    }

    private fun toNextQuestion(lastSelectedIndex: Int) {
        _showWrongHint[lastSelectedIndex].value = false
        _showCorrectHint[task.questions[currIndex].correctItemIndex].value = false

        ++currIndex
        if (currIndex == task.questions.size) {
            calcNextTime()
        } else {
            _answered.value = false
            currQuestion.value = task.questions[currIndex]
        }
    }

    private fun calcNextTime() {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            val timeDiff = endTime - startTime
            task.timeUsed = timeDiff / 1000
            withContext(Dispatchers.IO) {
                task.calcNextTime(dao)
            }
            _navigateToResult.value = true
        }
    }

    fun downNavToResult() {
        _navigateToResult.value = false
    }
}