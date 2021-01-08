package com.pepcy.nihongo.ui.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {
    private var _navigateToOther = MutableLiveData<Boolean?>()
    val navigateToOther: LiveData<Boolean?>
        get() = _navigateToOther

    fun navToOther() {
        _navigateToOther.value = true
    }
    fun downNavToOther() {
        _navigateToOther.value = false
    }
}