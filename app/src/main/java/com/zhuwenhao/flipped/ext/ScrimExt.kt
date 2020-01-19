package com.zhuwenhao.flipped.ext

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.LruCache
import android.view.Gravity
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private val cubicGradientScrimCache = LruCache<Int, Drawable>(10)

/**
 * Creates an approximated cubic gradient using a multi-stop linear gradient. See
 * [this page](https://github.com/romannurik/muzei/blob/master/main/src/main/java/com/google/android/apps/muzei/util/ScrimUtil.kt) for more
 * details.
 */
fun makeCubicGradientScrimDrawable(
        gravity: Int,
        alpha: Int = 0xFF,
        red: Int = 0x0,
        green: Int = 0x0,
        blue: Int = 0x0,
        requestedStops: Int = 8
): Drawable {
    var numStops = requestedStops

    // Generate a cache key by hashing together the inputs, based on the method described in the Effective Java book
    var cacheKeyHash = Color.argb(alpha, red, green, blue)
    cacheKeyHash = 31 * cacheKeyHash + numStops
    cacheKeyHash = 31 * cacheKeyHash + gravity

    val cachedGradient = cubicGradientScrimCache.get(cacheKeyHash)
    if (cachedGradient != null) {
        return cachedGradient
    }

    numStops = max(numStops, 2)

    val paintDrawable = PaintDrawable().apply {
        shape = RectShape()
    }

    val stopColors = IntArray(numStops)

    for (i in 0 until numStops) {
        val x = i * 1F / (numStops - 1)
        val opacity = x.toDouble().pow(3.0).toFloat().constrain(0F, 1F)
        stopColors[i] = Color.argb((alpha * opacity).toInt(), red, green, blue)
    }

    val x0: Float
    val x1: Float
    val y0: Float
    val y1: Float
    when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
        Gravity.START -> {
            x0 = 1F
            x1 = 0F
        }
        Gravity.END -> {
            x0 = 0F
            x1 = 1F
        }
        else -> {
            x0 = 0F
            x1 = 0F
        }
    }
    when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
        Gravity.TOP -> {
            y0 = 1F
            y1 = 0F
        }
        Gravity.BOTTOM -> {
            y0 = 0F
            y1 = 1F
        }
        else -> {
            y0 = 0F
            y1 = 0F
        }
    }

    paintDrawable.shaderFactory = object : ShapeDrawable.ShaderFactory() {
        override fun resize(width: Int, height: Int): Shader {
            return LinearGradient(
                    width * x0,
                    height * y0,
                    width * x1,
                    height * y1,
                    stopColors, null,
                    Shader.TileMode.CLAMP)
        }
    }

    cubicGradientScrimCache.put(cacheKeyHash, paintDrawable)
    return paintDrawable
}

fun Float.constrain(min: Float, max: Float): Float = max(min, min(max, this))