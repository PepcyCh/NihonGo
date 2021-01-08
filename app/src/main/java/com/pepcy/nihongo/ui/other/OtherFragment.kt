package com.pepcy.nihongo.ui.other

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.pepcy.nihongo.R
import com.pepcy.nihongo.databinding.FragmentOtherBinding

class OtherFragment : Fragment() {

    private lateinit var viewModel: OtherViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentOtherBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_other, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(OtherViewModel::class.java)

        viewModel.navigateToSettings.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(OtherFragmentDirections.actionNavOtherFragmentToSettingsFragment())
                viewModel.downNavToSettings()
            }
        }

        viewModel.navigateToAbout.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(OtherFragmentDirections.actionNavOtherFragmentToAboutFragment())
                viewModel.downNavToAbout()
            }
        }

        binding.otherViewModel = viewModel
        return binding.root
    }

}