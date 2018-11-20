package com.zhuwenhao.flipped.http

import com.orhanobut.logger.Logger
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject

class HttpLogger : HttpLoggingInterceptor.Logger {

    private val sb: StringBuilder = StringBuilder()

    override fun log(message: String) {
        var mMessage = message

        if (mMessage.startsWith("--> GET") || mMessage.startsWith("--> POST")) {
            sb.setLength(0)
        }

        if ((mMessage.startsWith("{") && mMessage.endsWith("}")) || (mMessage.startsWith("[") && mMessage.endsWith("]"))) {
            mMessage = when {
                mMessage.startsWith("{") -> JSONObject(mMessage).toString(4)
                mMessage.startsWith("[") -> JSONArray(mMessage).toString(4)
                else -> mMessage
            }
        }
        sb.append(mMessage.plus("\n"))
        if (mMessage.startsWith("<-- END HTTP")) {
            Logger.d(sb.toString())
        }
    }
}