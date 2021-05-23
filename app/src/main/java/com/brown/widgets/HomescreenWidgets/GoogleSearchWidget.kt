package com.brown.widgets.HomescreenWidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.widget.RemoteViews
import com.brown.widgets.R
import com.brown.widgets.helpers.bitmapWithCornerRadius

/**
 * Implementation of App Widget functionality.
 */
class GoogleSearchWidget : AppWidgetProvider() {
	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {
		// There may be multiple widgets active, so update all of them
		for (appWidgetId in appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId)
		}
	}

	companion object {
		fun updateAppWidget(
			context: Context,
			appWidgetManager: AppWidgetManager,
			appWidgetId: Int
		) {
			// Construct the RemoteViews object
			val views = RemoteViews(context.packageName, R.layout.google_search_widget)

			val cornerRadius = context.resources.getDimension(R.dimen.widget_corner_radius) * 1.225f
			val background = (context.resources.getDrawable(R.drawable.google_search_background) as BitmapDrawable).bitmap
			views.setImageViewBitmap(R.id.Background, bitmapWithCornerRadius(cornerRadius, background))

			val launchGoogleSearchIntent = let {
				val intent = Intent(Intent.ACTION_SEARCH).apply {
					component = ComponentName("com.google.android.googlequicksearchbox", "com.google.android.apps.gsa.queryentry.QueryEntryActivity")
				}
				PendingIntent.getActivity(context, appWidgetId, intent, 0)
			}
			views.setOnClickPendingIntent(R.id.GoogleSearchButton, launchGoogleSearchIntent)

			val launchVoiceAssistantIntent = let {
				val intent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
					flags = Intent.FLAG_ACTIVITY_NEW_TASK
				}
				PendingIntent.getActivity(context, appWidgetId, intent, 0)
			}
			views.setOnClickPendingIntent(R.id.VoiceAssitantButton, launchVoiceAssistantIntent)

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, views)
		}
	}
}