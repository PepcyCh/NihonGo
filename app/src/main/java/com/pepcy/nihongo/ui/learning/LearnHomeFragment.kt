package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.WordsDatabase
import com.pepcy.nihongo.databinding.FragmentLearnHomeBinding
import com.pepcy.nihongo.learning.WordSet

class LearnHomeFragment : Fragment(), WordSetClickListener {

    private lateinit var viewModel: LearnHomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLearnHomeBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_learn_home, container, false)
        binding.lifecycleOwner = this

        val adapter = LearnHomeAdapter(this)

        val viewModelFactory = LearnHomeViewModelFactory(WordsDatabase.getInstance(requireNotNull(this.activity).application).wordsDao())
        viewModel = ViewModelProvider(this, viewModelFactory).get(LearnHomeViewModel::class.java)

        binding.learnWordSetRecyclerView.adapter = adapter

        viewModel.getAllWordSets()
        viewModel.wordSets.observe(viewLifecycleOwner) {
            it.let {
                adapter.wordSets = it
            }
        }

        viewModel.navigateToNew.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(LearnHomeFragmentDirections.actionNavLearnHomeFragmentToNewSetFragment())
                viewModel.downNavToNew()
            }
        }

        viewModel.navigateToQuestion.observe(viewLifecycleOwner) {
            if (it == true) {
                val task = viewModel.selectedWordSet.generateTask()
                this.findNavController().navigate(LearnHomeFragmentDirections.actionNavLearnHomeFragmentToNavQuestionFragment(task))
                viewModel.downNavToQuestion()
            }
        }

        viewModel.navigateToSetting.observe(viewLifecycleOwner) {
            if (it == true) {
                val wordSetEntity = viewModel.selectedWordSet.getEntity()
                this.findNavController().navigate(
                        LearnHomeFragmentDirections.actionNavLearnHomeFragmentToNavSetSettingFragment(wordSetEntity))
                viewModel.downNavToSetting()
            }
        }

        viewModel.navigateToStat.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(
                        LearnHomeFragmentDirections.actionNavLearnHomeFragmentToSetStatFragment(viewModel.selectedWordSet.setName))
                viewModel.downNavToStat()
            }
        }

        binding.learnHomeViewModel = viewModel

        return binding.root
    }

    override fun onStartClick(wordSet: WordSet) {
        if (wordSet.cardsCount < 4) {
            Toast.makeText(context, R.string.learn_home_cannot_start, Toast.LENGTH_SHORT).show()
        } else if (!wordSet.needToReview) {
            Toast.makeText(context, R.string.learn_home_studied, Toast.LENGTH_SHORT).show()
        } else {
            viewModel.startTaskFromWordSet(wordSet)
        }
    }
    override fun onSettingClick(wordSet: WordSet) {
        viewModel.settingWordSet(wordSet)
    }

    override fun onStatClick(wordSet: WordSet) {
        if (!wordSet.studied) {
            Toast.makeText(context, R.string.learn_home_cannot_stat, Toast.LENGTH_SHORT).show()
        } else {
            viewModel.showWordSetStat(wordSet)
        }
    }
}