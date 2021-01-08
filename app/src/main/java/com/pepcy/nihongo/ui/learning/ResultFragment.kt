package com.pepcy.nihongo.ui.learning

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pepcy.nihongo.MainActivity
import com.pepcy.nihongo.R
import com.pepcy.nihongo.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private lateinit var viewModel: ResultViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentResultBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        binding.lifecycleOwner = this

        val arguments = ResultFragmentArgs.fromBundle(arguments)
        val viewModelFactory = ResultViewModelFactory(arguments.timeDiff, arguments.accuracy)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)

        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(ResultFragmentDirections.actionNavResultFragmentToNavLearnHomeFragment())
                (activity as MainActivity).forbidBackButton = false
                viewModel.downNavToHome()
            }
        }

        binding.resultTime.text = getString(R.string.result_time, viewModel.timeDiffMin, viewModel.timeDiffSec)
        binding.resultAccuracy.text = getString(R.string.result_accuracy, viewModel.accuracyPercent)

        binding.resultViewModel = viewModel

        return binding.root
    }
}