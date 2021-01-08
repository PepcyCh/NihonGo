package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel(private val timeDiff: Long, private val accuracy: Float) : ViewModel() {
    val timeDiffSec: Long
        get() = timeDiff % 60
    val timeDiffMin: Long
        get() = timeDiff / 60
    val accuracyPercent: Float
        get() = accuracy * 100

    private var _navigateToHome = MutableLiveData<Boolean?>()
    val navigateToHome: LiveData<Boolean?>
        get() = _navigateToHome

    fun navToHome() {
        _navigateToHome.value = true
    }

    fun downNavToHome() {
        _navigateToHome.value = false
    }
}