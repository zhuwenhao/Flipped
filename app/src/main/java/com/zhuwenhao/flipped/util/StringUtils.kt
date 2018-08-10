package com.zhuwenhao.flipped.util

import java.text.DecimalFormat

class StringUtils {

    companion object {
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