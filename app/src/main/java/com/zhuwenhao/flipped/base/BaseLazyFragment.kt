package com.zhuwenhao.flipped.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.support.RxFragment

abstract class BaseLazyFragment : RxFragment() {

    private var mIsVisible: Boolean = false
    private var mIsPrepared: Boolean = false
    private var mIsFirstLoad: Boolean = true

    protected lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContext = context!!
        mIsFirstLoad = true
        return inflater.inflate(provideLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mIsPrepared = true
        lazyLoad()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            mIsVisible = true
            onVisible()
        } else {
            mIsVisible = false
            onInvisible()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mIsVisible = true
            onVisible()
        } else {
            mIsVisible = false
            onInvisible()
        }
    }

    protected fun onVisible() {
        lazyLoad()
    }

    protected fun onInvisible() {
    }

    protected fun lazyLoad() {
        if (!mIsPrepared || !mIsVisible || !mIsFirstLoad)
            return

        mIsFirstLoad = false
        initData()
    }

    protected abstract fun provideLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initData()
}