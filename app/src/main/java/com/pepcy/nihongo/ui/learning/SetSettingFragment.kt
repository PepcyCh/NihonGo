package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.WordSetEntity
import com.pepcy.nihongo.database.WordsDatabase
import com.pepcy.nihongo.databinding.FragmentSetSettingBinding
import com.pepcy.nihongo.learning.WordSet

class SetSettingFragment : Fragment() {

    private lateinit var viewModel: SetSettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSetSettingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_setting, container, false)
        binding.lifecycleOwner = this

        val arguments = SetSettingFragmentArgs.fromBundle(arguments)
        val setSettingViewModelFactory = SetSettingViewModelFactory(
                WordsDatabase.getInstance(requireNotNull(this.activity).application).wordsDao(),
                arguments.wordSetEntity, requireContext())
        viewModel = ViewModelProvider(this, setSettingViewModelFactory).get(SetSettingViewModel::class.java)

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(SetSettingFragmentDirections.actionNavSetSettingFragmentToNavLearnHomeFragment())
                viewModel.downNavToHome()
            }
        }

        viewModel.toastInfo.observe(viewLifecycleOwner) {
            it.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        val addSetDialogFragment = AddSetDialogFragment(viewModel)
        binding.setAddSetBtn.setOnClickListener {
            addSetDialogFragment.show(parentFragmentManager, "add_set_dialog")
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
        viewModel.addSetName.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.addSet()
            }
        }

        binding.setSettingViewModel = viewModel

        return binding.root
    }
}