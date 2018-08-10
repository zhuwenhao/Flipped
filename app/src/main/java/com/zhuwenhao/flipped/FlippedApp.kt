package com.zhuwenhao.flipped

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

class FlippedApp : Application() {

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}