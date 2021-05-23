package com.brown.widgets

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.brown.widgets.Receivers.BatteryReceiver
import com.brown.widgets.Receivers.MediaReceiver

class BroadcastMonitorService : Service() {
	private val TAG = BroadcastMonitorService::class.simpleName

	// Receivers
	private val batteryReceiver = BatteryReceiver()
	private var mediaReceiver: MediaReceiver? = null

	// Notification constants
	private val NOTIFICATION_CHANNEL_ID = TAG.toString()
	private val NOTIFICATION_CHANNEL_NAME = "Widget service"
	private val NOTIFICATION_DESCRIPTION = "Allows widgets to be updated consistently"
	private val NOTIFICATION_MESSAGE = "Tap this to open the app"
	private val NOTIFICATION_COLOR = Color.DKGRAY

	// Notification values
	private lateinit var notificationManager: NotificationManager
	private lateinit var notificationChannel: NotificationChannel
	private lateinit var notification: Notification

	private fun init() {
		notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationChannel = NotificationChannel(
			NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE
		).apply {
			description = NOTIFICATION_DESCRIPTION
			lightColor = NOTIFICATION_COLOR
			lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
		}
		notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
			.setOngoing(true)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setPriority(NotificationCompat.PRIORITY_MIN)
			.setCategory(NotificationCompat.CATEGORY_SERVICE)
			.setContentText(NOTIFICATION_MESSAGE)
			.setContentIntent(
				PendingIntent.getActivity(
					this, 0,
					Intent(this, MainActivity::class.java), 0
				)
			)
			.build()
	}

	override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
		init()
		notificationManager.createNotificationChannel(notificationChannel)
		startForeground(startId, notification)

		registerReceiver(batteryReceiver, IntentFilter().apply {
			addAction(Intent.ACTION_BATTERY_CHANGED)
			addAction(Intent.ACTION_BATTERY_LOW)
			addAction(Intent.ACTION_BATTERY_OKAY)
		})
		mediaReceiver = MediaReceiver(applicationContext)

		return START_STICKY
	}

	override fun onDestroy() {
		notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)
		unregisterReceiver(batteryReceiver)
		mediaReceiver?.disconnect()
	}

	override fun onBind(intent: Intent): IBinder? { return null }
}