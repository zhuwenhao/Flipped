package com.zhuwenhao.flipped.view.callback

import com.kingja.loadsir.callback.Callback
import com.zhuwenhao.flipped.R

class LoadingCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_loading
    }
}