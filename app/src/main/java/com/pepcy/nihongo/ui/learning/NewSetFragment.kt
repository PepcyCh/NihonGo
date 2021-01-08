package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.WordsDatabase
import com.pepcy.nihongo.databinding.FragmentNewSetBinding

class NewSetFragment : Fragment() {

    private lateinit var viewModel: NewSetViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNewSetBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_set, container, false)
        binding.lifecycleOwner = this

        val viewModelFactory = NewSetViewModelFactory(
            WordsDatabase.getInstance(requireNotNull(this.activity).application).wordsDao(), requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewSetViewModel::class.java)

        viewModel.toastInfo.observe(viewLifecycleOwner) {
            it.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(NewSetFragmentDirections.actionNewSetFragmentToNavLearnHomeFragment())
                viewModel.downNavToHome()
            }
        }

        var insertDialog: InsertWordDialogFragment? = null
        viewModel.insertCount.observe(viewLifecycleOwner) {
            when {
                it == 0 -> {
                    insertDialog = InsertWordDialogFragment(viewModel.insertCountMax.value!!)
                    insertDialog!!.show(parentFragmentManager, "insert_word_dialog")
                }
                it == viewModel.insertCountMax.value -> {
                    insertDialog!!.dismiss()
                }
                it > 0 -> {
                    insertDialog!!.setProgress(it)
                }
            }
        }

        binding.newSetNameEdit.doAfterTextChanged { newText ->
            viewModel.queryDuplicatedName(newText.toString())
        }

        val baseSetDialogFragment = BaseSetDialogFragment(viewModel, resources.getStringArray(R.array.base_set_names))
        binding.newSetBaseSet.setOnClickListener {
            baseSetDialogFragment.show(parentFragmentManager, "base_set_dialog")
        }

        binding.newSetViewModel = viewModel

        return binding.root
    }
}