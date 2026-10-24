package com.example.monthgrid.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import android.view.View

object SystemActionHelper {

    fun performLightHaptic(view: View, enabled: Boolean) {
        if (!enabled) return
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
    }

    fun shareCalendarText(context: Context, subject: String, calendarText: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, calendarText)
        }
        val chooser = Intent.createChooser(sendIntent, subject).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
