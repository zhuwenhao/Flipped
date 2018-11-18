package com.zhuwenhao.flipped

import android.app.Application
import com.kingja.loadsir.core.LoadSir
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.zhuwenhao.flipped.view.callback.EmptyCallback
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import com.zhuwenhao.flipped.view.callback.LoadingCallback
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

        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .tag(getString(R.string.app_name))
                .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        LoadSir.beginBuilder()
                .addCallback(LoadingCallback())
                .addCallback(EmptyCallback())
                .addCallback(ErrorCallback())
                .setDefaultCallback(LoadingCallback::class.java)
                .commit()
    }
}