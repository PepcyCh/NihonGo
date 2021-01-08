package com.pepcy.nihongo.ui.query

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.WordsDatabase
import com.pepcy.nihongo.databinding.FragmentWordBinding

class WordFragment : Fragment() {

    private lateinit var viewModel: WordViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentWordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_word, container, false)
        binding.lifecycleOwner = this

        val arguments = WordFragmentArgs.fromBundle(arguments)
        val dao = WordsDatabase.getInstance(requireNotNull(this.activity).application).wordsDao()
        val viewModelFactory = WordViewModelFactory(dao, arguments.word)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WordViewModel::class.java)

        val adapter = WordAdapter(arguments.word)

        binding.wordAddToSet.setOnClickListener {
            viewModel.fetchWordSetNames()
        }
        viewModel.namesReady.observe(viewLifecycleOwner) {
            if (it == true) {
                AddToSetDialogFragment(viewModel, viewModel.wordSetNames)
                        .show(parentFragmentManager, "add_to_set_dialog")
            }
        }

        viewModel.dupCard.observe(viewLifecycleOwner) {
            if (it == true) {
                Toast.makeText(context, R.string.word_dup_card, Toast.LENGTH_SHORT).show()
            }
        }

        binding.wordViewModel = viewModel
        binding.wordMeaningRecyclerView.adapter = adapter

        return binding.root
    }

}