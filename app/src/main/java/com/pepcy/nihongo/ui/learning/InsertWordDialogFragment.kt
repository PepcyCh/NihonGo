package com.pepcy.nihongo.ui.learning

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.pepcy.nihongo.R

class InsertWordDialogFragment(private val progressMax: Int) : DialogFragment() {
    private lateinit var hintText: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_progress_insert, null)

            val builder = AlertDialog.Builder(it)
            builder.setView(view)

            hintText = view.findViewById(R.id.insert_hint)
            hintText.text = getString(R.string.insert_progress_hit, 0, progressMax)
            builder.create()
        } ?: throw IllegalStateException("Activity can not be null")
    }

    fun setProgress(progress: Int) {
        hintText.text = getString(R.string.insert_progress_hit, progress, progressMax)
    }
}