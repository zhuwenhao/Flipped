package com.zhuwenhao.flipped.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.jaeger.library.StatusBarUtil
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.zhuwenhao.flipped.R

@SuppressLint("Registered")
abstract class BaseSubActivity : RxAppCompatActivity() {

    protected lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        mContext = this
        Slidr.attach(this, SlidrConfig.Builder().edge(true).build())
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), 0)
        initView()
        initData()
    }

    protected abstract fun provideLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initData()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}