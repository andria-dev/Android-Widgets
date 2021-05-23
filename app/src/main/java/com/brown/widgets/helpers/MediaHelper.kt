package com.brown.widgets.helpers
import android.graphics.*

fun bitmapWithCornerRadius(cornerRadius: Float, bitmap: Bitmap): Bitmap {
	return Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
		.also { roundedBitmap ->
			Canvas(roundedBitmap).apply {
				val paint = Paint()
				paint.isAntiAlias = true
				paint.shader =
					BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
				val size = (
					if (bitmap.width > bitmap.height) bitmap.height
					else bitmap.width
				).toFloat()
				drawRoundRect(
					RectF(0f, 0F, size, size),
					cornerRadius,
					cornerRadius,
					paint
				)
			}
		}
}
