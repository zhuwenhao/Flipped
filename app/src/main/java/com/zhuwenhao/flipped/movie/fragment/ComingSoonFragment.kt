package com.zhuwenhao.flipped.movie.fragment

import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseLazyFragment

class ComingSoonFragment : BaseLazyFragment() {

    companion object {
        fun newInstance(): ComingSoonFragment {
            return ComingSoonFragment()
        }
    }

    override fun provideLayoutId(): Int {
        return R.layout.fragment_coming_soon
    }

    override fun initView() {

    }

    override fun initData() {

    }
}