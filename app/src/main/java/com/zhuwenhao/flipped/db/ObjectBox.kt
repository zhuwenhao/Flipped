package com.zhuwenhao.flipped.db

import android.content.Context
import com.zhuwenhao.flipped.BuildConfig
import com.zhuwenhao.flipped.widget.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser

object ObjectBox {

    lateinit var boxStore: BoxStore
        private set

    fun build(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()
        if (BuildConfig.DEBUG) {
            AndroidObjectBrowser(boxStore).start(context.applicationContext)
        }
    }
}