package com.deniscerri.ytdl.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.preference.PreferenceManager
import com.deniscerri.ytdl.util.WhatsAppUtil

class WhatsAppSendReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val fileUriString = intent.getStringExtra(EXTRA_FILE_URI) ?: return
        val fileUri = Uri.parse(fileUriString)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val contactJid = if (preferenceGetAutoSend(context)) {
            preferences.getString("whatsapp_contact", "") ?: ""
        } else {
            null // Will open contact picker
        }

        if (contactJid.isNullOrBlank()) {
            WhatsAppUtil.sendToContactPicker(context, fileUri)
        } else {
            WhatsAppUtil.sendToWhatsApp(context, fileUri, contactJid)
        }
    }

    private fun preferenceGetAutoSend(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("whatsapp_auto_send", false)
    }

    companion object {
        const val EXTRA_FILE_URI = "file_uri"
    }
}
