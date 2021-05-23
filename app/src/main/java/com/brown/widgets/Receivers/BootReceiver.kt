package com.brown.widgets.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brown.widgets.BroadcastMonitorService

class BootReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action == Intent.ACTION_BOOT_COMPLETED)
			context.startForegroundService(
				Intent(context, BroadcastMonitorService::class.java)
			)
	}
}