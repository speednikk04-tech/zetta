package com.deniscerri.ytdl.ui.more.settings

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.deniscerri.ytdl.R

class WhatsAppSettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceScreen?.let { screen ->
            if (screen.findPreference<androidx.preference.Preference>(CreditsFooterPreference.KEY) == null) {
                screen.addPreference(CreditsFooterPreference(requireContext()))
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(listView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, v.paddingBottom + systemBars.bottom)
            insets
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.whatsapp_preferences, rootKey)

        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Update summaries
        findPreference<androidx.preference.SwitchPreferenceCompat>("whatsapp_enabled")?.apply {
            summaryProvider = androidx.preference.Preference.SummaryProvider<androidx.preference.SwitchPreferenceCompat> {
                if (isChecked) getString(R.string.whatsapp_enabled) else getString(R.string.whatsapp_disabled)
            }
        }

        findPreference<androidx.preference.EditTextPreference>("whatsapp_contact")?.apply {
            text = preferences.getString("whatsapp_contact", "")
            summaryProvider = androidx.preference.Preference.SummaryProvider<androidx.preference.EditTextPreference> {
                val phone = text
                if (phone.isNullOrBlank()) getString(R.string.whatsapp_contact_not_set) else phone
            }
        }

        findPreference<androidx.preference.EditTextPreference>("whatsapp_message_template")?.apply {
            text = preferences.getString("whatsapp_message_template", "")
            summaryProvider = androidx.preference.Preference.SummaryProvider<androidx.preference.EditTextPreference> {
                val template = text
                if (template.isNullOrBlank()) getString(R.string.whatsapp_message_template_empty) else template
            }
        }
    }
}
