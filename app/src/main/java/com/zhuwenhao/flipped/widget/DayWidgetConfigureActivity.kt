package com.zhuwenhao.flipped.widget

import android.appwidget.AppWidgetManager
import android.view.Menu
import android.view.MenuItem
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import com.zhuwenhao.flipped.db.DateWidget
import com.zhuwenhao.flipped.db.DateWidget_
import com.zhuwenhao.flipped.db.ObjectBox
import kotlinx.android.synthetic.main.layout_toolbar.*

class DayWidgetConfigureActivity : BaseSubActivity() {

    private var widgetId = -1

    private val child by lazy { supportFragmentManager.findFragmentById(R.id.content) as DayWidgetConfigureFragment }

    override fun provideLayoutId(): Int {
        return R.layout.activity_day_widget_configure
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white)

        val extras = intent.extras
        if (extras != null)
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        val dwBox = ObjectBox.boxStore.boxFor(DateWidget::class.java)
        val dateWidget = dwBox.query().equal(DateWidget_.widgetId, widgetId.toLong()).build().findFirst()

        /*btnCreate.setOnClickListener {
            if (dateWidget != null) {
                dateWidget.title = editTitle.text.toString()
                dwBox.put(dateWidget)
            }

            val intent1 = Intent(this, DayWidgetConfigureActivity::class.java)
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            val view = RemoteViews(packageName, R.layout.widget_day)
            view.setOnClickPendingIntent(R.id.content, pendingIntent)
            view.setTextViewText(R.id.textTitle, editTitle.text.toString())
            appWidgetManager.updateAppWidget(widgetId, view)

            val intent = Intent()
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }*/
    }

    override fun initData() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_day_widget_configure, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = child.onOptionsItemSelected(item)
}