package com.zhuwenhao.flipped.ext

import android.content.SharedPreferences

fun SharedPreferences.putString(key: String, value: String) {
    this.edit().putString(key, value).apply()
}

fun SharedPreferences.putStringSet(key: String, values: Set<String>) {
    this.edit().putStringSet(key, values).apply()
}

fun SharedPreferences.putInt(key: String, value: Int) {
    this.edit().putInt(key, value).apply()
}

fun SharedPreferences.putLong(key: String, value: Long) {
    this.edit().putLong(key, value).apply()
}

fun SharedPreferences.putFloat(key: String, value: Float) {
    this.edit().putFloat(key, value).apply()
}

fun SharedPreferences.putBoolean(key: String, value: Boolean) {
    this.edit().putBoolean(key, value).apply()
}

fun SharedPreferences.remove(key: String) {
    this.edit().remove(key).apply()
}

fun SharedPreferences.clear() {
    this.edit().clear().apply()
}

fun SharedPreferences.getStringX(key: String, defValue: String = "") = this.getString(key, defValue)!!

fun SharedPreferences.getStringSetX(key: String, defValues: Set<String> = emptySet()): Set<String> = this.getStringSet(key, defValues)!!

fun SharedPreferences.getIntX(key: String, defValue: Int = 0) = this.getInt(key, defValue)

fun SharedPreferences.getLongX(key: String, defValue: Long = 0L) = this.getLong(key, defValue)

fun SharedPreferences.getFloatX(key: String, defValue: Float = 0F) = this.getFloat(key, defValue)

fun SharedPreferences.getBooleanX(key: String, defValue: Boolean = false) = this.getBoolean(key, defValue)