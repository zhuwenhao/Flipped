package com.zhuwenhao.flipped.http

import com.orhanobut.logger.Logger
import okhttp3.logging.HttpLoggingInterceptor

class HttpLogger : HttpLoggingInterceptor.Logger {

    private val sb: StringBuilder = StringBuilder()

    override fun log(message: String) {
        var mMessage = message

        if (mMessage.startsWith("--> GET") || mMessage.startsWith("--> POST")) {
            sb.setLength(0)
        }

        if ((mMessage.startsWith("{") && mMessage.endsWith("}")) || (mMessage.startsWith("[") && mMessage.endsWith("]"))) {
            mMessage = JsonUtils.formatJson(JsonUtils.decodeUnicode(mMessage))
        }
        sb.append(mMessage.plus("\n"))
        if (mMessage.startsWith("<-- END HTTP")) {
            Logger.d(sb.toString())
        }
    }
}