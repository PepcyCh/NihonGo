package com.pepcy.nihongo.ui.learning

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.pepcy.nihongo.R

class BaseSetDialogFragment(
    private val viewModel: NewSetViewModel,
    private val baseSetShowArray: Array<String>,
) : DialogFragment() {
    private val baseSetArray = arrayOf("empty", "jp4", "jp3", "jp2", "jp1", "test")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.base_set_title)
                    .setItems(R.array.base_set_names) { _, which ->
                        viewModel.baseSetName.value = baseSetArray[which]
                        viewModel.baseSetShowName.value = baseSetShowArray[which]
                    }
                    .setPositiveButton(R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity can not be null")
    }
}