package com.zhuwenhao.flipped.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhuwenhao.flipped.R

class CustomLoadMoreView : BaseLoadMoreView() {

    override fun getRootView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.layout_custom_load_more, parent, false)
    }

    override fun getLoadingView(holder: BaseViewHolder): View = holder.getView(R.id.loadMoreLoading)

    override fun getLoadComplete(holder: BaseViewHolder): View = holder.getView(R.id.loadMoreComplete)

    override fun getLoadEndView(holder: BaseViewHolder): View = holder.getView(R.id.loadMoreEnd)

    override fun getLoadFailView(holder: BaseViewHolder): View = holder.getView(R.id.loadMoreFail)
}