package com.pepcy.nihongo.ui.learning

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pepcy.nihongo.R

class WaitingDialogFragment(private val message: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
            builder.create()
        } ?: throw IllegalStateException("Activity can not be null")
    }
}