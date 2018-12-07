package com.zhuwenhao.flipped.extension

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.zhuwenhao.flipped.db.DatabaseOpenHelper

val Context.database: DatabaseOpenHelper
    get() = DatabaseOpenHelper.getInstance(applicationContext)

fun Context.getDefaultSp(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}