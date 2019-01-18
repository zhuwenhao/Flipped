package com.zhuwenhao.flipped.ext

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

fun Context.getDefaultSp(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}