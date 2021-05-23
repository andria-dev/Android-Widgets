package com.brown.widgets.Receivers

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brown.widgets.HomescreenWidgets.BatteryWidget
import com.brown.widgets.helpers.BatteryInfo
import com.brown.widgets.helpers.DataReceiverHelper

class BatteryReceiver : BroadcastReceiver() {
	private val TAG = BatteryReceiver::class.simpleName

	override fun onReceive(context: Context, intent: Intent) {
		val battery = BatteryInfo(intent)

		DataReceiverHelper.updateWidget<BatteryWidget>(context) {
		manager: AppWidgetManager, id: Int ->
			BatteryWidget.updateAppWidget(context, manager, id, battery)
		}
	}
}