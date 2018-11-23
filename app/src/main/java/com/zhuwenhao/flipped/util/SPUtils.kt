package com.zhuwenhao.flipped.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R

class SPUtils {

    companion object {
        private fun getSP(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        private fun getEditor(context: Context): SharedPreferences.Editor {
            return getSP(context).edit()
        }

        fun getString(context: Context, key: String, defValue: String): String {
            return getSP(context).getString(key, defValue)!!
        }

        fun putString(context: Context, key: String, value: String) {
            getEditor(context).putString(key, value).apply()
        }

        fun getLastMovieCity(context: Context): String {
            return getString(context, Constants.SP_KEY_LAST_MOVIE_CITY, context.getString(R.string.default_movie_city))
        }
    }
}