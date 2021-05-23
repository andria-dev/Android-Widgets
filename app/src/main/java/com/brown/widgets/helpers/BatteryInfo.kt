package com.brown.widgets.helpers

import android.content.Intent
import android.os.BatteryManager

class BatteryInfo(batteryIntent: Intent?) : Intent(batteryIntent) {
	companion object Status {
		val CHARGING = "CHARGING"
		val DISCHARGING = "DISCHARGING"
		val UNKNOWN = "UNKNOWN"
	}

	private var _chargingStatus = UNKNOWN
	private var _chargingIndicator = ""
	private var _level = 0

	val chargingStatus: String
		get() { return _chargingStatus }
	val isCharging: Boolean
		get() { return _chargingStatus == CHARGING }
	val chargingIndicator: String
		get() { return _chargingIndicator }
	val level: Int
		get() { return _level }

	init {
		_chargingStatus = getIntExtra(
			BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN
		).let { status ->
			when (status) {
				BatteryManager.BATTERY_STATUS_CHARGING,
				BatteryManager.BATTERY_STATUS_FULL -> CHARGING
				BatteryManager.BATTERY_STATUS_DISCHARGING,
				BatteryManager.BATTERY_STATUS_NOT_CHARGING -> DISCHARGING
				else -> UNKNOWN
			}
		}

		_chargingIndicator = when(_chargingStatus) {
			CHARGING -> "Charging"
			DISCHARGING -> "Not charging"
			else -> "Not sure"
		}

		_level = let {
			val level = getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
			val scale = getIntExtra(BatteryManager.EXTRA_SCALE, -1)
			level * 100 / scale
		}
	}
}