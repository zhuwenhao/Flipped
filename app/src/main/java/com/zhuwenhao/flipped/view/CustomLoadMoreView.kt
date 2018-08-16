package com.zhuwenhao.flipped.view

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.zhuwenhao.flipped.R

class CustomLoadMoreView : LoadMoreView() {

    override fun getLayoutId(): Int {
        return R.layout.layout_custom_load_more
    }

    override fun getLoadingViewId(): Int {
        return R.id.loadMoreLoading
    }

    override fun getLoadEndViewId(): Int {
        return R.id.loadMoreEnd
    }

    override fun getLoadFailViewId(): Int {
        return R.id.loadMoreFail
    }
}