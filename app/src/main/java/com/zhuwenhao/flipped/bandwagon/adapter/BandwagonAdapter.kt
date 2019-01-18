package com.zhuwenhao.flipped.bandwagon.adapter

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.bandwagon.Bandwagon

class BandwagonAdapter(data: List<Bandwagon>) : BaseItemDraggableAdapter<Bandwagon, BaseViewHolder>(R.layout.item_bandwagon, data) {

    override fun convert(helper: BaseViewHolder, item: Bandwagon) {
        helper.setText(R.id.textTitle, item.title)
        helper.setText(R.id.textIpAddresses, item.ipAddresses)
        helper.setText(R.id.textNodeLocation, item.nodeLocation)

        helper.addOnClickListener(R.id.imgEdit)
    }
}