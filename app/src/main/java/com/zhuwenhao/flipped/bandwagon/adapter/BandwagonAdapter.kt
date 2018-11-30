package com.zhuwenhao.flipped.bandwagon.adapter

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.bandwagon.entity.Bandwagon

class BandwagonAdapter(data: MutableList<Bandwagon>) : BaseItemDraggableAdapter<Bandwagon, BaseViewHolder>(R.layout.item_bandwagon, data) {

    override fun convert(helper: BaseViewHolder, item: Bandwagon) {
        helper.setText(R.id.textTitle, item.title)

        helper.addOnClickListener(R.id.imgEdit)
    }
}