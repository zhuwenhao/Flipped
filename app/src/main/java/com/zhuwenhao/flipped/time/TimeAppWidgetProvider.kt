package com.zhuwenhao.flipped.time

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.extension.getDefaultSp
import com.zhuwenhao.flipped.extension.putString
import com.zhuwenhao.flipped.extension.remove

class TimeAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Logger.d("onUpdate --> ${Gson().toJson(appWidgetIds)}")
        val data = Gson().fromJson<ArrayList<TimeWidget>>(context.getDefaultSp().getString("widget_data", ""), object : TypeToken<ArrayList<TimeWidget>>(){}.type)
        for (id in appWidgetIds) {
            val intent = Intent(context, TimeAppWidgetConfigureActivity::class.java)
            intent.putExtra("widgetId", id)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val views = RemoteViews(context.packageName, R.layout.appwidget_time)
            views.setOnClickPendingIntent(R.id.content, pendingIntent)

            appWidgetManager.updateAppWidget(id, views)

            data.add(TimeWidget(id, ""))
        }
        context.getDefaultSp().putString("widget_data", Gson().toJson(data))
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Logger.d("onRestored")
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        context.getDefaultSp().putString("widget_data", "[]")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        context.getDefaultSp().remove("widget_data")
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Logger.d("onDeleted --> ${Gson().toJson(appWidgetIds)}")
        val data = Gson().fromJson<ArrayList<TimeWidget>>(context.getDefaultSp().getString("widget_data", ""), object : TypeToken<ArrayList<TimeWidget>>(){}.type)
        for (id in appWidgetIds) {
            for ((index, value) in data.withIndex()) {
                if (id == value.id) {
                    data.removeAt(index)
                    break
                }
            }
        }
        context.getDefaultSp().putString("widget_data", Gson().toJson(data))
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Logger.d("onAppWidgetOptionsChanged")
    }
}