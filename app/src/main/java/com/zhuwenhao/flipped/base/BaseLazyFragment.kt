package com.zhuwenhao.flipped.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle3.components.support.RxFragment

abstract class BaseLazyFragment : RxFragment() {

    protected lateinit var mContext: Context

    private var isFirstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContext = context!!
        return inflater.inflate(provideLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstLoad) {
            isFirstLoad = false
            initData()
        }
    }

    protected abstract fun provideLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initData()
}