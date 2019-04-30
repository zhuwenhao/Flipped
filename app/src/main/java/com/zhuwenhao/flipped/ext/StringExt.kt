package com.zhuwenhao.flipped.ext

fun joinNonEmpty(separator: CharSequence, vararg elements: CharSequence): String {
    val builder = StringBuilder()
    var first = true
    for (element in elements) {
        if (element.isEmpty()) {
            continue
        }
        if (first) {
            first = false
        } else {
            builder.append(separator)
        }
        builder.append(element)
    }
    return builder.toString()
}

fun List<String>.containsElseFirst(text: CharSequence): String = when {
    isEmpty() -> ""
    size == 1 -> first()
    else -> {
        forEach {
            if (it.contains(text)) {
                return it
            }
        }
        first()
    }
}
