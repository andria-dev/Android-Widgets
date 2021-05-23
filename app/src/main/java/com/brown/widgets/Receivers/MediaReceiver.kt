package com.brown.widgets.Receivers

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import com.brown.widgets.HomescreenWidgets.MediaPlayerWidget
import com.brown.widgets.helpers.DataReceiverHelper
import com.brown.widgets.helpers.NotifyHelper
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector.ConnectionListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException
import com.spotify.android.appremote.api.error.UserNotAuthorizedException

class MediaWidgetReceiver(private val callback: (action: String?) -> Unit) : BroadcastReceiver() {
	companion object {
		val ACTION_PLAY = "MEDIA_WIDGET_PLAY"
		val ACTION_PAUSE = "MEDIA_WIDGET_PAUSE"
	}
	override fun onReceive(context: Context, intent: Intent) {
		callback(intent.action)
	}
}

class MediaReceiver(private val context: Context) {
	private val TAG = MediaReceiver::class.simpleName
	private var spotify: SpotifyAppRemote? = null
	private val mediaWidgetReceiver = MediaWidgetReceiver { action ->
		when (action) {
			MediaWidgetReceiver.ACTION_PLAY -> spotify?.playerApi?.resume()
			MediaWidgetReceiver.ACTION_PAUSE -> spotify?.playerApi?.pause()
		}
	}

	private val makeToast = NotifyHelper.toastGenerator(context)

	private val SPOTIFY_CLIENT_ID = "d62624b4efbc47688bc8c613ab77a534"
	private val CONNECTION_PARAMS = ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
		.setRedirectUri("com.brown.widgets://")
		.showAuthView(true)
		.build()

	init {
		initializeSpotify()
		context.registerReceiver(mediaWidgetReceiver, IntentFilter().apply {
			addAction(MediaWidgetReceiver.ACTION_PAUSE)
			addAction(MediaWidgetReceiver.ACTION_PLAY)
		})
	}

	@Suppress("DEPRECATION")
	val waitingForSpotifyHandler = Handler()
	private fun initializeSpotify() {
		SpotifyAppRemote.connect(context, CONNECTION_PARAMS, object : ConnectionListener {
			override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
				spotify = spotifyAppRemote
				// Now you can start interacting with App Remote
				subscribe()
			}

			override fun onFailure(throwable: Throwable) {
				Log.e(TAG, throwable.message, throwable)
				when (throwable) {
					is CouldNotFindSpotifyApp ->
						makeToast("Spotify is not installed on this device.", true)
					is NotLoggedInException ->
						makeToast("You aren't logged in to Spotify.", true)
					is UserNotAuthorizedException ->
						makeToast("You haven't authorized your widget to interact with Spotify.", true)
					is SpotifyConnectionTerminatedException -> {
						spotify = null
						// Try to restart SpotifyAppRemote connection
						waitingForSpotifyHandler.postDelayed(object : Runnable {
							override fun run() {
								if (spotify != null) return

								initializeSpotify()
								waitingForSpotifyHandler.postDelayed(this, 5000)
							}
						}, 5000)
					}
					else ->
						makeToast("Unexpected error when connecting Music widget to Spotify.", true)
				}
			}
		})
	}

	private var previousAlbumArt: Bitmap? = null
	private fun subscribe() {
		spotify?.playerApi?.subscribeToPlayerState()
			?.setEventCallback { playerState ->
				val isPaused = playerState.isPaused
				DataReceiverHelper.updateWidget<MediaPlayerWidget>(context) { manager: AppWidgetManager, id: Int ->
					MediaPlayerWidget.updateAppWidget(context, manager, id, isPaused, previousAlbumArt)
				}

				val imageUri = playerState.track.imageUri
				var albumArt: Bitmap? = null

				// New image URI to use
				spotify?.imagesApi?.getImage(imageUri)
					?.setResultCallback { bitmap ->
						albumArt = bitmap
						previousAlbumArt = albumArt
						DataReceiverHelper.updateWidget<MediaPlayerWidget>(context) { manager: AppWidgetManager, id: Int ->
							MediaPlayerWidget.updateAppWidget(context, manager, id, isPaused, albumArt)
						}
					}
					?.setErrorCallback { error ->
						Log.e(TAG, error.message ?: "No error message")
					}
			}
	}

	fun disconnect() {
		SpotifyAppRemote.disconnect(spotify)
	}
}