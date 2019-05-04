package com.zhuwenhao.flipped.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.zhuwenhao.flipped.R

@SuppressLint("Registered")
abstract class BaseSubActivity : RxAppCompatActivity() {

    protected lateinit var mContext: Context

    protected lateinit var loadService: LoadService<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        mContext = this
        if (isSupportSwipeBack()) {
            Slidr.attach(this, SlidrConfig.Builder().edge(isSwipeBackEdgeOnly()).build())
            StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 0)
        }
        initView()
        initData()
    }

    protected abstract fun provideLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initData()

    protected fun initLoadSir(view: View) {
        loadService = LoadSir.getDefault().register(view)
    }

    protected fun initLoadSir(view: View, listener: Callback.OnReloadListener) {
        loadService = LoadSir.getDefault().register(view, listener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    open fun isSupportSwipeBack() = true

    open fun isSwipeBackEdgeOnly() = false
}