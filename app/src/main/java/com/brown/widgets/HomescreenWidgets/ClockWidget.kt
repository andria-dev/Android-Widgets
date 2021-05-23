package com.brown.widgets.HomescreenWidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.brown.widgets.R

/**
 * Implementation of App Widget functionality.
 */
class ClockWidget : AppWidgetProvider() {
	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {
		for (widgetId in appWidgetIds) {
			updateAppWidget(context, appWidgetManager, widgetId)
		}
	}

	companion object {
		fun updateAppWidget(
			context: Context,
			appWidgetManager: AppWidgetManager,
			appWidgetId: Int
		) {
			// Construct the RemoteViews object
			val views = RemoteViews(context.packageName, R.layout.clock_widget)

			// Open clock when widget is touched
			val intentToLaunchClock = Intent(Intent.ACTION_MAIN)
				.apply { setPackage("com.google.android.deskclock") }
			val pendingIntent =
				PendingIntent.getActivity(context, appWidgetId, intentToLaunchClock, 0)
			views.setOnClickPendingIntent(R.id.Root, pendingIntent)

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, views)
		}
	}
}