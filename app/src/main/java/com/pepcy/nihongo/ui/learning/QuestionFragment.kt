package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pepcy.nihongo.MainActivity
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.WordsDatabase
import com.pepcy.nihongo.databinding.FragmentQuestionBinding
import com.pepcy.nihongo.learning.Task

class QuestionFragment : Fragment() {

    private lateinit var viewModel: QuestionViewModel
    private lateinit var task: Task

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        binding.lifecycleOwner = this

        (activity as MainActivity).forbidBackButton = true

        val arguments = QuestionFragmentArgs.fromBundle(arguments)
        task = arguments.task

        val viewModelFactory = QuestionViewModelFactory(
                WordsDatabase.getInstance(requireNotNull(this.activity).application).wordsDao(), task)
        viewModel = ViewModelProvider(this, viewModelFactory).get(QuestionViewModel::class.java)

        viewModel.navigateToResult.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(
                        QuestionFragmentDirections.actionNavQuestionFragmentToNavResultFragment(viewModel.timeUsed, viewModel.accuracy))
                viewModel.downNavToResult()
            }
        }

        observerButtonBackground(binding)

        binding.questionViewModel = viewModel

        return binding.root
    }

    private fun observerButtonBackground(binding: FragmentQuestionBinding) {
        viewModel.showWrongHint(0).observe(viewLifecycleOwner, Observer {
            binding.questionButton1.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_wrong else R.color.answer_unchosen))
        })
        viewModel.showWrongHint(1).observe(viewLifecycleOwner, Observer {
            binding.questionButton2.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_wrong else R.color.answer_unchosen))
        })
        viewModel.showWrongHint(2).observe(viewLifecycleOwner, Observer {
            binding.questionButton3.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_wrong else R.color.answer_unchosen))
        })
        viewModel.showWrongHint(3).observe(viewLifecycleOwner, Observer {
            binding.questionButton4.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_wrong else R.color.answer_unchosen))
        })

        viewModel.showCorrectHint(0).observe(viewLifecycleOwner, Observer {
            binding.questionButton1.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_correct else R.color.answer_unchosen))
        })
        viewModel.showCorrectHint(1).observe(viewLifecycleOwner, Observer {
            binding.questionButton2.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_correct else R.color.answer_unchosen))
        })
        viewModel.showCorrectHint(2).observe(viewLifecycleOwner, Observer {
            binding.questionButton3.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_correct else R.color.answer_unchosen))
        })
        viewModel.showCorrectHint(3).observe(viewLifecycleOwner, Observer {
            binding.questionButton4.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    if (it == true) R.color.answer_correct else R.color.answer_unchosen))
        })
    }
}