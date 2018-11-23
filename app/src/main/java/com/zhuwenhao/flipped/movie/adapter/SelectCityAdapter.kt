package com.zhuwenhao.flipped.movie.adapter

import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.movie.entity.City

class SelectCityAdapter(data: List<City>) : BaseMultiItemQuickAdapter<City, BaseViewHolder>(data) {

    init {
        addItemType(City.TYPE_HEADER, R.layout.item_select_city_header)
        addItemType(City.TYPE_DATA, R.layout.item_select_city)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, City.TYPE_HEADER)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        FullSpanUtil.onViewAttachedToWindow(holder, this, City.TYPE_DATA)
    }

    override fun convert(helper: BaseViewHolder, item: City) {
        when (helper.itemViewType) {
            City.TYPE_HEADER -> {
                helper.setText(R.id.textHeader, item.alphabet)
            }
            City.TYPE_DATA -> {
                helper.setText(R.id.textCity, item.name)
                helper.setGone(R.id.imgCheck, item.isChecked)
            }
        }
    }
}