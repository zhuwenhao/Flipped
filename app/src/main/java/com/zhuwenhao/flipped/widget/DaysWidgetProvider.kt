package com.zhuwenhao.flipped.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.widget.RemoteViews
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.db.ObjectBox
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.DateTimeFormat

class DaysWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Logger.d("onUpdate --> ${Gson().toJson(appWidgetIds)}")

        val dwBox = ObjectBox.boxStore.boxFor(DaysWidget::class.java)
        val dwList = dwBox.query().build().find()

        for (widgetId in appWidgetIds) {
            for (dw in dwList) {
                if (widgetId == dw.widgetId) {
                    val intent = Intent(context, DaysWidgetConfigureActivity::class.java)
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                    val pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    val views = RemoteViews(context.packageName, when (dw.textAlignment) {
                        0 -> R.layout.widget_days_top_left
                        1 -> R.layout.widget_days_top_right
                        2 -> R.layout.widget_days_bottom_left
                        3 -> R.layout.widget_days_bottom_right
                        4 -> R.layout.widget_days_center
                        5 -> R.layout.widget_days_center_top
                        6 -> R.layout.widget_days_center_bottom
                        7 -> R.layout.widget_days_center_left
                        8 -> R.layout.widget_days_center_right
                        else -> R.layout.widget_days_center
                    })
                    views.setOnClickPendingIntent(R.id.content, pendingIntent)
                    views.setTextViewText(R.id.textTitle, dw.title)
                    views.setTextViewTextSize(R.id.textTitle, TypedValue.COMPLEX_UNIT_SP, dw.titleSize.toFloat())
                    views.setTextColor(R.id.textTitle, Color.parseColor(dw.titleColor))
                    views.setTextViewText(R.id.textDays, if (dw.countdown) {
                        if (dw.startDate == DateTime.now().toString("yyyy-MM-dd")) {
                            context.getString(R.string.days_widget_days_today)
                        } else {
                            context.getString(R.string.days_widget_days_left, Period(DateTime.now(), DateTime.parse(dw.startDate, DateTimeFormat.forPattern("yyyy-MM-dd")), PeriodType.days()).days + 1)
                        }
                    } else {
                        context.getString(R.string.days_widget_days, Period(DateTime.parse(dw.startDate, DateTimeFormat.forPattern("yyyy-MM-dd")), DateTime.now(), PeriodType.days()).days + 1)
                    })
                    views.setTextViewTextSize(R.id.textDays, TypedValue.COMPLEX_UNIT_SP, dw.daysSize.toFloat())
                    views.setTextColor(R.id.textDays, Color.parseColor(dw.daysColor))

                    appWidgetManager.updateAppWidget(widgetId, views)
                    break
                }
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val dwBox = ObjectBox.boxStore.boxFor(DaysWidget::class.java)
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