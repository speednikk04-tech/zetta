package com.deniscerri.ytdl.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.deniscerri.ytdl.R

object WhatsAppUtil {

    private const val WHATSAPP_PACKAGE = "com.whatsapp"
    private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"

    fun isWhatsAppInstalled(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(WHATSAPP_PACKAGE, PackageManager.PackageInfoFlags.of(0))
                true
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(WHATSAPP_PACKAGE, 0)
                true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getPackageInfo(WHATSAPP_BUSINESS_PACKAGE, PackageManager.PackageInfoFlags.of(0))
                    true
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(WHATSAPP_BUSINESS_PACKAGE, 0)
                    true
                }
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    fun sendToWhatsApp(context: Context, fileUri: Uri, contactJid: String? = null) {
        if (!isWhatsAppInstalled(context)) {
            Toast.makeText(context, context.getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = getMimeType(context, fileUri)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage(WHATSAPP_PACKAGE)
            if (!contactJid.isNullOrBlank()) {
                // Format: phone number without + prefix
                val formattedJid = contactJid.replace("[^0-9]".toRegex(), "")
                putExtra("jid", "$formattedJid@s.whatsapp.net")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to WhatsApp Business
            intent.setPackage(WHATSAPP_BUSINESS_PACKAGE)
            try {
                context.startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(context, context.getString(R.string.whatsapp_send_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendToContactPicker(context: Context, fileUri: Uri) {
        if (!isWhatsAppInstalled(context)) {
            Toast.makeText(context, context.getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = getMimeType(context, fileUri)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage(WHATSAPP_PACKAGE)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            intent.setPackage(WHATSAPP_BUSINESS_PACKAGE)
            try {
                context.startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(context, context.getString(R.string.whatsapp_send_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getMimeType(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri) ?: when {
            uri.toString().endsWith(".mp4") -> "video/mp4"
            uri.toString().endsWith(".mp3") -> "audio/mpeg"
            uri.toString().endsWith(".jpg") || uri.toString().endsWith(".jpeg") -> "image/jpeg"
            uri.toString().endsWith(".png") -> "image/png"
            else -> "*/*"
        }
    }
}
