package com.zhuwenhao.flipped.bandwagon.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.bandwagon.Bandwagon

class BandwagonAdapter(data: MutableList<Bandwagon>) : BaseQuickAdapter<Bandwagon, BaseViewHolder>(R.layout.item_bandwagon, data), DraggableModule {

    override fun convert(holder: BaseViewHolder, item: Bandwagon) {
        holder.setText(R.id.textTitle, item.title)
        holder.setText(R.id.textIpAddresses, item.ipAddresses)
        holder.setText(R.id.textNodeLocation, item.nodeLocation)
    }
}