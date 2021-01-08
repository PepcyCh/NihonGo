package com.pepcy.nihongo.ui.query

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.database.WordsDatabaseUtil
import com.pepcy.nihongo.word.Word

class AddToSetDialogFragment(
        private val viewModel: WordViewModel,
        private val names: ArrayList<String>
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.add_to_set_title)
                    .setItems(names.toTypedArray()) { _, which ->
                        viewModel.addToSet(names[which])
                    }
                    .setPositiveButton(R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity can not be null")
    }
}