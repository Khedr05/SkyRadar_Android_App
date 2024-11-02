package com.example.skyradar.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skyradar.R
import com.example.skyradar.database.AlarmLocalDataSourceImpl
import com.example.skyradar.database.LocationLocalDataSourceImpl
import com.example.skyradar.databinding.FragmentSettingsBinding
import com.example.skyradar.model.RepositoryImpl
import com.example.skyradar.network.RemoteDataSourceImpl
import com.example.skyradar.network.RetrofitInstance
import com.example.skyradar.settings.viewmodel.SettingsFactory
import com.example.skyradar.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = SettingsFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(RetrofitInstance.retrofit),
                LocationLocalDataSourceImpl.getInstance(requireContext()),
                AlarmLocalDataSourceImpl.getInstance(requireContext())
            )
        )

        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        // Set the ViewModel for data binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Collect language and unit values and set default selections if available
        lifecycleScope.launchWhenStarted {
            launch { viewModel.language.collect { savedLanguage -> setupLanguageSpinner(savedLanguage) } }
            launch { viewModel.unit.collect { savedUnit -> setupUnitSpinner(savedUnit) } }
        }
    }

    private fun setupLanguageSpinner(savedLanguage: String?) {
        // Define the display names and corresponding language codes
        val languageOptions = listOf("Arabic", "English", "Mobile Language")
        val languageCodeMap = mapOf("Arabic" to "ar", "English" to "en", "Mobile Language" to "Default Mobile Language")
        val languageAdapter = SettingsAdapter(requireContext(), languageOptions)
        binding.spinnerLanguage.adapter = languageAdapter

// Set saved language as default selection if available
        savedLanguage?.let {
            val displayLanguage = languageCodeMap.entries.find { entry -> entry.value == it }?.key
            val position = languageOptions.indexOf(displayLanguage)
            if (position >= 0) {
                binding.spinnerLanguage.setSelection(position)
            }
        }

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the language code corresponding to the selected display name
                val selectedLanguageCode = languageCodeMap[languageOptions[position]]
                selectedLanguageCode?.let { viewModel.saveLanguage(it) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupUnitSpinner(savedUnit: String?) {
        val unitOptions = listOf("Standard", "Imperial", "Metric")
        val unitAdapter = SettingsAdapter(requireContext(), unitOptions)
        binding.spinnerUnits.adapter = unitAdapter

        // Set saved unit as default selection if available
        savedUnit?.let {
            val position = unitOptions.indexOf(it)
            if (position >= 0) {
                binding.spinnerUnits.setSelection(position)
            }
        }

        binding.spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.saveUnit(unitOptions[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // No need to set binding to null as it's not a view binding
    }
}
