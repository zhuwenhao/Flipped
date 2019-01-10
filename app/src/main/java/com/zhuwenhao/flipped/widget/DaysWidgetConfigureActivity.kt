package com.zhuwenhao.flipped.widget

import android.view.Menu
import android.view.MenuItem
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import kotlinx.android.synthetic.main.layout_toolbar.*

class DaysWidgetConfigureActivity : BaseSubActivity() {

    private val child by lazy { supportFragmentManager.findFragmentById(R.id.content) as DaysWidgetConfigureFragment }

    override fun provideLayoutId(): Int {
        return R.layout.activity_days_widget_configure
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)
    }

    override fun initData() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_days_widget_configure, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = child.onOptionsItemSelected(item)
}