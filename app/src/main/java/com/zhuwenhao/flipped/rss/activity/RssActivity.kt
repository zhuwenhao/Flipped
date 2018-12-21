package com.zhuwenhao.flipped.rss.activity

import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import kotlinx.android.synthetic.main.layout_toolbar.*

class RssActivity : BaseSubActivity() {

    override fun provideLayoutId(): Int {
        return R.layout.activity_rss
    }

    override fun initView() {
        setSupportActionBar(toolbar)
    }

    override fun initData() {

    }
}