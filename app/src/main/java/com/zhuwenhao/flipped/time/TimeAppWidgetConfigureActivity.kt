package com.zhuwenhao.flipped.time

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViews
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import kotlinx.android.synthetic.main.activity_time_app_widget_configure.*

class TimeAppWidgetConfigureActivity : BaseSubActivity() {

    private var widgetId = -1

    override fun provideLayoutId(): Int {
        return R.layout.activity_time_app_widget_configure
    }

    override fun initView() {
        btnCreate.setOnClickListener {
            val intent1 = Intent(this, TimeAppWidgetConfigureActivity::class.java)
            intent1.putExtra("widgetId", widgetId)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            val view = RemoteViews(packageName, R.layout.appwidget_time)
            view.setOnClickPendingIntent(R.id.content, pendingIntent)
            view.setTextViewText(R.id.textTitle, editTitle.text.toString())
            appWidgetManager.updateAppWidget(widgetId, view)

            val intent = Intent()
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun initData() {
        val extras = intent.extras
        if (extras != null)
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    }
}