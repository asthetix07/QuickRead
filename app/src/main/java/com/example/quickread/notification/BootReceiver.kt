package com.example.quickread.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-schedules the periodic news notification worker after device reboot.
 *
 * WorkManager persists its own database across reboots, but the system may
 * clear pending work on some OEMs. This receiver acts as a safety net to
 * guarantee notifications resume after a restart.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            NotificationScheduler.schedule(context)
        }
    }
}
