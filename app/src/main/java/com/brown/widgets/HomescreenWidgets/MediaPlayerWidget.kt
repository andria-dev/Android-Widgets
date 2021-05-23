package com.brown.widgets.HomescreenWidgets

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.RemoteViews
import com.brown.widgets.R
import com.brown.widgets.Receivers.MediaWidgetReceiver
import com.brown.widgets.helpers.bitmapWithCornerRadius

/**
 * Implementation of Media Player widget functionality.
 */
class MediaPlayerWidget : AppWidgetProvider() {
	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {
		// There may be multiple widgets active, so update all of them
		for (appWidgetId in appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId, null, null)
		}
	}

	override fun onEnabled(context: Context) {
		// Enter relevant functionality for when the first widget is created
	}

	override fun onDisabled(context: Context) {
		// Enter relevant functionality for when the last widget is disabled
	}

	companion object {
		val TAG = MediaPlayerWidget::class.simpleName
		@SuppressLint("UseCompatLoadingForDrawables")
		fun updateAppWidget(
			context: Context,
			appWidgetManager: AppWidgetManager,
			appWidgetId: Int,
			isPaused: Boolean?,
			currentArtwork: Bitmap?
		) {
			// Construct the RemoteViews object
			val views = RemoteViews(context.packageName, R.layout.media_player_widget)

			// Update album art image
			val cornerRadius = context.resources.getDimension(R.dimen.widget_corner_radius) * 1.225f
			val albumArt = currentArtwork
				?: (context.resources.getDrawable(R.drawable.album_art_placeholder) as BitmapDrawable).bitmap

			views.setImageViewBitmap(R.id.AlbumArt, bitmapWithCornerRadius(cornerRadius, albumArt))

			// Update play pause button
			views.setInt(
				R.id.PlayPauseButton, "setImageResource",
				if (isPaused == false) R.drawable.pause
				else R.drawable.play_arrow
			)

			// Handle play pause functionality
			val playPauseIntent = let {
				val intent = Intent(
					if (isPaused == false) MediaWidgetReceiver.ACTION_PAUSE
					else MediaWidgetReceiver.ACTION_PLAY
				)
				PendingIntent.getBroadcast(context, appWidgetId, intent, 0)
			}
			views.setOnClickPendingIntent(R.id.PlayPauseButton, playPauseIntent)

			// Open Spotify when clicked
			val openSpotifyIntent = let {
				val intent = Intent(Intent.ACTION_MAIN).apply {
					component = ComponentName("com.spotify.music", "com.spotify.music.MainActivity")
				}
				PendingIntent.getActivity(context, appWidgetId, intent, 0)
			}
			views.setOnClickPendingIntent(R.id.AlbumArt, openSpotifyIntent)

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, views)
		}
	}
}
