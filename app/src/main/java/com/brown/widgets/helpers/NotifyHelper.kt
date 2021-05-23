package com.brown.widgets.helpers

import android.content.Context
import android.widget.Toast

object NotifyHelper {
	fun toastGenerator(context: Context): (String, Boolean) -> Unit {
		return fun(message: String, isDurationShort: Boolean) {
			Toast.makeText(context, message, (
				if (isDurationShort) Toast.LENGTH_SHORT
				else Toast.LENGTH_LONG
			)).show()
		}
	}
}