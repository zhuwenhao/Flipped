package com.zhuwenhao.flipped.base

import android.annotation.SuppressLint
import android.os.Bundle
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity

@SuppressLint("Registered")
abstract class BaseActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        initView()
        initData()
    }

    protected abstract fun provideLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initData()
}