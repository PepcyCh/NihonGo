package com.pepcy.nihongo.ui.query

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.pepcy.nihongo.MainActivity
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.ConfigDatabase
import com.pepcy.nihongo.database.WordsDatabase
import com.pepcy.nihongo.databinding.FragmentQueryBinding
import com.pepcy.nihongo.ui.learning.WaitingDialogFragment
import com.pepcy.nihongo.word.Word

class QueryFragment : Fragment(), WordClickListener {

    private lateinit var viewModel: QueryViewModel
    private lateinit var binding: FragmentQueryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_query, container, false)
        binding.lifecycleOwner = this

        val adapter = QueryAdapter(this)

        val viewModelFactory = QueryViewModelFactory(
            WordsDatabase.getInstance(requireNotNull(this.activity).application).wordsDao(),
            ConfigDatabase.getInstance(requireNotNull(this.activity).application).configDao(),
            resources
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(QueryViewModel::class.java)

        if (!(activity as MainActivity).inited) {
            viewModel.loadConfig()
            (activity as MainActivity).inited = true
        }
        viewModel.lang.observe(viewLifecycleOwner) {
            it?.let {
                (activity as MainActivity).changeLang(it)
            }
        }

        var initWaitingDialogFragment: WaitingDialogFragment? = null
        viewModel.showInitWaitingDialog.observe(viewLifecycleOwner) {
            if (it == true) {
                initWaitingDialogFragment = WaitingDialogFragment(viewModel.dialogMessage.value!!)
                initWaitingDialogFragment!!.show(parentFragmentManager, "init_waiting_dialog")
            }
        }
        viewModel.dismissInitWaitingDialog.observe(viewLifecycleOwner) {
            if (it == true && initWaitingDialogFragment != null) {
                initWaitingDialogFragment!!.dismiss()
                viewModel.downDismissWaitingDialog()
            }
        }

        binding.queryListRecyclerView.adapter = adapter
        binding.queryListRecyclerView.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))

        binding.queryWordEdit.doAfterTextChanged { newText ->
            viewModel.queryWord.value = newText.toString()
        }

        viewModel.queryWord.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.queryWord(it)
            }
        }
        viewModel.wordList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.data = it
            }
        }
        viewModel.navigateToWord.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(QueryFragmentDirections.actionNavQueryFragmentToNavWordFragment(viewModel.selectedWord))
                viewModel.doneNavToWordFragment()
            }
        }

        binding.queryViewModel = viewModel

        return binding.root
    }

    override fun onWordClickListener(word: Word) {
        binding.queryViewModel?.clickWord(word)
    }
}