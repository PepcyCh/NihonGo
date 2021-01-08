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
import com.pepcy.nihongo.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAboutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)

        viewModel.navigateToOther.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(AboutFragmentDirections.actionAboutFragmentToNavOtherFragment())
                viewModel.downNavToOther()
            }
        }

        binding.aboutViewModel = viewModel

        return binding.root
    }
}