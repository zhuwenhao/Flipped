package com.zhuwenhao.flipped.util

import com.zhuwenhao.flipped.FlippedApp
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.movie.entity.Subject
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.DateTimeFormat
import java.text.DecimalFormat
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
                    mainlandDateTime.toString("M月d日") + " " + mainlandDateTime.dayOfWeek().asShortText
            } else {
                if (mainlandDate.length == 7)
                    mainlandDateTime.toString("yyyy年M月") + " 待定"
                else
                    mainlandDateTime.toString("yyyy年M月d日") + " " + mainlandDateTime.dayOfWeek().asShortText
            }
        }

        fun formatYearAndGenres(year: String, genres: List<String>): String {
            val sb = StringBuilder()
            sb.append(year)
            if (genres.isEmpty())
                return sb.toString()

            sb.append(" / ")
            for (genre in genres) {
                sb.append(genre)
                sb.append(" ")
            }
            return sb.toString()
        }

        fun formatDirectors(directorList: List<Subject.Director>): String {
            val sb = StringBuilder()
            for (director in directorList) {
                sb.append(director.name)
                sb.append(" ")
            }
            return sb.toString()
        }

        fun formatCasts(castList: List<Subject.Cast>): String {
            val sb = StringBuilder()
            for (cast in castList) {
                sb.append(cast.name)
                sb.append(" ")
            }
            return sb.toString()
        }

        fun formatIpAddresses(ipAddresses: List<String>): String {
            val sb = StringBuilder()

            for ((index, value) in ipAddresses.withIndex()) {
                sb.append(value)
                if (index < ipAddresses.size - 1)
                    sb.append(",")
            }
            return sb.toString()
        }

        fun firstLetterToUpper(str: String): String {
            val chars = str.toCharArray()
            if (chars[0] in 'a'..'z') {
                chars[0] = chars[0] - 32
            }
            return String(chars)
        }

        fun formatByte(size: Long): String {
            var b = size.toFloat()
            val format = DecimalFormat("#.##")

            //B
            if (b < 1024) {
                return format.format(b) + "B"
            } else {
                b /= 1024
            }

            //KB
            if (b < 1024) {
                return format.format(b) + "KB"
            } else {
                b /= 1024
            }

            //MB
            return if (b < 1024) {
                format.format(b) + "MB"
            } else {
                format.format(b / 1024) + "GB"
            }
        }
    }
}