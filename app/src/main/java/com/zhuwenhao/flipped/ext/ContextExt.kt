package com.zhuwenhao.flipped.ext

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.TypedValue

/**
 * dp to px
 *
 * @param dpValue dp
 * @return px
 */
fun Context.dpToPx(dpValue: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, resources.displayMetrics).toInt()
}

/**
 * sp to px
 *
 * @param spValue sp
 * @return px
 */
fun Context.spToPx(spValue: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, resources.displayMetrics).toInt()
}

fun Context.getDefaultSp(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}