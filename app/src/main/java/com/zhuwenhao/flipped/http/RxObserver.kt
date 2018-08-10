package com.zhuwenhao.flipped.http

import android.net.ParseException
import com.google.gson.JsonParseException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

abstract class RxObserver<T> : Observer<T> {

    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        if (e is HttpException || (e is ConnectException || e is SocketException || e is SocketTimeoutException || e is SSLHandshakeException || e is UnknownHostException))
            onFailure(Exception("请求失败"))
        else if (e is JSONException || e is JsonParseException || e is ParseException)
            onFailure(Exception("解析错误"))
        else
            onFailure(Exception("未知错误"))
    }

    override fun onComplete() {

    }

    abstract fun onSuccess(t: T)

    abstract fun onFailure(e: Exception)
}