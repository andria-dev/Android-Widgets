package com.brown.widgets.HomescreenWidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.RemoteViews
import com.brown.widgets.R
import com.brown.widgets.helpers.BatteryInfo

/**
 * Implementation of Battery Widget functionality.
 */
class BatteryWidget : AppWidgetProvider() {
	private val TAG = BatteryWidget::class.simpleName

	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {
		// get battery info
		val battery = BatteryInfo(context.registerReceiver(null,
			IntentFilter(Intent.ACTION_BATTERY_CHANGED)
		))

		// update all widgets with battery info
		for (widgetId in appWidgetIds) {
			updateAppWidget(context, appWidgetManager, widgetId, battery)
		}
	}

	companion object {
		fun updateAppWidget(
			context: Context,
			appWidgetManager: AppWidgetManager,
			appWidgetId: Int,
			battery: BatteryInfo
		) {
			// Construct the RemoteViews object
			val views = RemoteViews(context.packageName, R.layout.battery_widget)

			// Update text
			views.setTextViewText(R.id.ChargingText, battery.chargingIndicator)
			views.setTextViewText(R.id.BatteryPercentage, "${battery.level}")

			// Update battery meter
			when {
				battery.isCharging -> R.id.BatteryMeter_charging
				battery.chargingStatus == BatteryInfo.UNKNOWN -> R.id.BatteryMeter_unknown
				battery.level > 35 -> R.id.BatteryMeter_default
				else -> R.id.BatteryMeter_critical
			}.let { matchingId ->
				val variants = arrayOf(
					R.id.BatteryMeter_default,
					R.id.BatteryMeter_charging,
					R.id.BatteryMeter_critical,
					R.id.BatteryMeter_unknown
				)
				for (variantId in variants) {
					val visibility = when (variantId) {
						matchingId -> View.VISIBLE
						else -> View.GONE
					}
					views.setInt(variantId, "setVisibility", visibility)
					views.setInt(variantId, "setProgress", battery.level)
				}
			}

			// Launch battery settings on touch
			val intentToLaunchBatterySettings = Intent(Intent.ACTION_MAIN).apply {
				component = ComponentName("com.android.settings", "com.android.settings.Settings\$PowerUsageSummaryActivity")
			}
			val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intentToLaunchBatterySettings, 0)
			views.setOnClickPendingIntent(R.id.Root, pendingIntent)

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, views)
		}
	}
}
