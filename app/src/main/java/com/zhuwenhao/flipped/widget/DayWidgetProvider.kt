package com.zhuwenhao.flipped.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.db.DateWidget
import com.zhuwenhao.flipped.db.ObjectBox

class DayWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Logger.d("onUpdate --> ${Gson().toJson(appWidgetIds)}")

        val dwBox = ObjectBox.boxStore.boxFor(DateWidget::class.java)
        val dwList = dwBox.query().build().find()

        for (widgetId in appWidgetIds) {
            var position = -1
            for ((index, dw) in dwList.withIndex()) {
                if (widgetId == dw.widgetId) {
                    position = index
                }
            }
            if (position == -1) {
                dwBox.put(DateWidget(widgetId = widgetId, title = ""))
            }

            val intent = Intent(context, DayWidgetConfigureActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val views = RemoteViews(context.packageName, R.layout.widget_day)
            views.setOnClickPendingIntent(R.id.content, pendingIntent)
            if (position != -1) {
                views.setTextViewText(R.id.textTitle, dwList[position].title)
            }

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val dwBox = ObjectBox.boxStore.boxFor(DateWidget::class.java)
        val dwList = dwBox.query().build().find()
        for (id in appWidgetIds) {
            for (dw in dwList) {
                if (id == dw.widgetId) {
                    dwBox.remove(dw.id)
                }
            }
        }
    }
}