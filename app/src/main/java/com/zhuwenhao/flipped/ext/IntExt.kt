package com.zhuwenhao.flipped.ext

fun Int.toColorHex() = String.format("#%08X", this).toUpperCase()