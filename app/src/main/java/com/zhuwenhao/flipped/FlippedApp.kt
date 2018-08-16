package com.zhuwenhao.flipped

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

class FlippedApp : Application() {

    init {
        INSTANCE = this
    }

    companion object {
        private lateinit var INSTANCE: FlippedApp

        fun getInstance(): FlippedApp {
            return INSTANCE
        }
    }

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}