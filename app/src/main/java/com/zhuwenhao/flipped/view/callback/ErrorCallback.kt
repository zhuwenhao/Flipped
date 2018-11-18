package com.zhuwenhao.flipped.view.callback

import com.kingja.loadsir.callback.Callback
import com.zhuwenhao.flipped.R

class ErrorCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_error
    }
}