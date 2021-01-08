package com.pepcy.nihongo.ui.other

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pepcy.nihongo.R

class LanguageSettingsDialogFragment(
    private val viewModel: SettingsViewModel,
    private val langShowNames: Array<String>,
) : DialogFragment() {
    private val langNames = arrayOf("zh", "en")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.base_set_title)
                .setItems(langShowNames) { _, which ->
                    viewModel.langName.value = langNames[which]
                    viewModel.langNameShow.value = langShowNames[which]
                }
                .setPositiveButton(R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity can not be null")
    }
}