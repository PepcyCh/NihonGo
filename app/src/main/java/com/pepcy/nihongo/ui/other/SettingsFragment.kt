package com.pepcy.nihongo.ui.other

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.pepcy.nihongo.MainActivity
import com.pepcy.nihongo.R
import com.pepcy.nihongo.database.ConfigDatabase
import com.pepcy.nihongo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.lifecycleOwner = this

        val viewModelFactory = SettingsViewModelFactory(ConfigDatabase.getInstance(requireNotNull(this.activity).application).configDao())
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
        viewModel.getConfig()

        val langSetDialogFragment = LanguageSettingsDialogFragment(viewModel, resources.getStringArray(R.array.lang_names))
        binding.settingsLangBtn.setOnClickListener {
            langSetDialogFragment.show(parentFragmentManager, "lang_set_dialog")
        }

        viewModel.navigateToOther.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToNavOtherFragment())
                viewModel.downNavToOther()
            }
        }

        viewModel.langName.observe(viewLifecycleOwner) {
            (activity as MainActivity).changeLang(it)
            viewModel.saveConfig()
        }

        binding.settingsViewModel = viewModel

        return binding.root
    }
}