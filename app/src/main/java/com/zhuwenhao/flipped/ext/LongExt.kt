package com.zhuwenhao.flipped.ext

import java.text.DecimalFormat

fun Long.formatByte(): String {
    var b = this.toFloat()
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