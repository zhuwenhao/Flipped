package com.zhuwenhao.flipped.bandwagon.activity

import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import kotlinx.android.synthetic.main.layout_toolbar.*

class BandwagonActivity : BaseSubActivity() {

    override fun provideLayoutId(): Int {
        return R.layout.activity_bandwagon
    }

    override fun initView() {
        setSupportActionBar(toolbar)
    }

    override fun initData() {

    }
}