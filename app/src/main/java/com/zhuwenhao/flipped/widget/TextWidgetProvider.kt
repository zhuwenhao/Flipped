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

class TextWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Logger.d("onUpdate --> ${Gson().toJson(appWidgetIds)}")

        val twBox = ObjectBox.boxStore.boxFor(TextWidget::class.java)
        val twList = twBox.query().build().find()

        for (widgetId in appWidgetIds) {
            for (tw in twList) {
                if (widgetId == tw.widgetId) {
                    val intent = Intent(context, TextWidgetConfigureActivity::class.java)
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                    val pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    val views = RemoteViews(context.packageName, when (tw.textAlignment) {
                        0 -> R.layout.widget_text_top_left
                        1 -> R.layout.widget_text_top_right
                        2 -> R.layout.widget_text_bottom_left
                        3 -> R.layout.widget_text_bottom_right
                        4 -> R.layout.widget_text_center
                        5 -> R.layout.widget_text_center_top
                        6 -> R.layout.widget_text_center_bottom
                        7 -> R.layout.widget_text_center_left
                        8 -> R.layout.widget_text_center_right
                        else -> R.layout.widget_text_center
                    })
                    views.setOnClickPendingIntent(R.id.textTitle, pendingIntent)
                    views.setTextViewText(R.id.textTitle, tw.title)
                    views.setTextViewTextSize(R.id.textTitle, TypedValue.COMPLEX_UNIT_SP, tw.titleSize.toFloat())
                    views.setTextColor(R.id.textTitle, Color.parseColor(tw.titleColor))

                    appWidgetManager.updateAppWidget(widgetId, views)
                    break
                }
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val twBox = ObjectBox.boxStore.boxFor(TextWidget::class.java)
        val twList = twBox.query().build().find()
        for (id in appWidgetIds) {
            for (tw in twList) {
                if (id == tw.widgetId) {
                    twBox.remove(tw.id)
                }
            }
        }
    }
}