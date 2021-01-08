package com.pepcy.nihongo.ui.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OtherViewModel : ViewModel() {
    private var _navigateToSettings = MutableLiveData<Boolean?>()
    val navigateToSettings: LiveData<Boolean?>
        get() = _navigateToSettings

    private var _navigateToAbout = MutableLiveData<Boolean?>()
    val navigateToAbout: LiveData<Boolean?>
        get() = _navigateToAbout

    fun navToSettings() {
        _navigateToSettings.value = true
    }
    fun downNavToSettings() {
        _navigateToSettings.value = false
    }

    fun navToAbout() {
        _navigateToAbout.value = true
    }
    fun downNavToAbout() {
        _navigateToAbout.value = false
    }
}