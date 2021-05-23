package com.brown.widgets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.brown.widgets.helpers.NotifyHelper

enum class ManagerAction {
	TOGGLE_MONITORING,
	MONITORING_CHANGE
}
enum class ManagerStatus {
	MONITORING,
	STOPPED,
	UNKNOWN
}

interface IManager {
	fun startMonitoring()
	fun stopMonitoring()
	fun sendMonitorAction(action: ManagerAction): ManagerStatus
}

class MainActivity : AppCompatActivity(), IManager {
	private val TAG = MainActivity::class.simpleName
	private val makeToast = NotifyHelper.toastGenerator(this)

	private fun getBroadcastMonitorIntent(context: Context): Intent {
		return Intent(context, BroadcastMonitorService::class.java)
	}
	override fun startMonitoring() {
		this.startForegroundService(getBroadcastMonitorIntent(this))
	}
	override fun stopMonitoring() {
		this.stopService(getBroadcastMonitorIntent(this))
	}

	private var status = ManagerStatus.MONITORING
	override fun sendMonitorAction(action: ManagerAction): ManagerStatus {
		status = when(action) {
			ManagerAction.TOGGLE_MONITORING -> when (status) {
				ManagerStatus.MONITORING -> {
					stopMonitoring()
					ManagerStatus.STOPPED
				}
				ManagerStatus.STOPPED -> {
					startMonitoring()
					ManagerStatus.MONITORING
				}
				else -> status
			}
			ManagerAction.MONITORING_CHANGE -> status
		}
		makeToast("New status: $status", true)
		return status
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Start foreground sticky service to update widgets
		startMonitoring()

		setContentView(R.layout.activity_main)
		setSupportActionBar(findViewById(R.id.toolbar))
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.action_settings -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
}