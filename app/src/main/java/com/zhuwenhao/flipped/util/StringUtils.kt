package com.zhuwenhao.flipped.util

import com.zhuwenhao.flipped.FlippedApp
import com.zhuwenhao.flipped.R
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.DateTimeFormat
import java.util.*

class StringUtils {

    companion object {
        fun formatMainlandPubDate(dateTime: DateTime): String {
            val days = Period(DateTime.now(), dateTime, PeriodType.days()).days
            val thisWeekdays = 7 - DateTime.now().dayOfWeek

            return when {
                days < thisWeekdays -> FlippedApp.getInstance().getString(R.string.this_week_release, dateTime.dayOfWeek().getAsShortText(Locale.CHINA))
                days < thisWeekdays + 7 -> FlippedApp.getInstance().getString(R.string.next_week_release, dateTime.dayOfWeek().getAsShortText(Locale.CHINA))
                else -> FlippedApp.getInstance().getString(R.string.other_release, dateTime.toString("yyyy-MM-dd"))
            }
        }

        fun getMainlandDate(pubDates: List<String>): String {
            var date = ""
            for (pubDate in pubDates) {
                if (pubDate.contains("中国大陆")) {
                    date = pubDate.split("(")[0]
                }
            }

            return date
        }

        fun getMainlandDateTime(mainlandDate: String): DateTime {
            return when {
                mainlandDate.isEmpty() -> DateTime(1995, 11, 10, 0, 0)
                mainlandDate.length == 7 -> DateTime.parse(mainlandDate, DateTimeFormat.forPattern("yyyy-MM"))
                else -> DateTime.parse(mainlandDate, DateTimeFormat.forPattern("yyyy-MM-dd"))
            }
        }

        fun getMainlandDateForHeader(mainlandDate: String, mainlandDateTime: DateTime): String {
            return if (mainlandDateTime.year == DateTime.now().year) {
                if (mainlandDate.length == 7)
                    mainlandDateTime.toString("M月") + " 待定"
                else
                    mainlandDateTime.toString("M月d日") + " " + mainlandDateTime.dayOfWeek().getAsShortText(Locale.CHINA)
            } else {
                if (mainlandDate.length == 7)
                    mainlandDateTime.toString("yyyy年M月") + " 待定"
                else
                    mainlandDateTime.toString("yyyy年M月d日") + " " + mainlandDateTime.dayOfWeek().getAsShortText(Locale.CHINA)
            }
        }
    }
}