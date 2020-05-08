package com.zhuwenhao.flipped.movie

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhuwenhao.flipped.GlideApp
import com.zhuwenhao.flipped.R

class Top250Adapter : BaseQuickAdapter<Subject, BaseViewHolder>(R.layout.item_movie_subject), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: Subject) {
        GlideApp.with(context).load(item.images.large).into(holder.getView(R.id.imgCover))

        holder.setText(R.id.textTitle, "${holder.adapterPosition + 1}. ${item.title}")
        if (item.rating.average == 0.0) {
            holder.setText(R.id.textDouBanRating, R.string.no_rating)
            holder.setGone(R.id.textRating, true)
        } else {
            holder.setText(R.id.textDouBanRating, R.string.dou_ban_rating)
            holder.setGone(R.id.textRating, false)
            holder.setText(R.id.textRating, item.rating.average.toString())
        }
        holder.setText(R.id.textYearAndGenres, item.yearAndGenres)
        holder.setText(R.id.textDirectors, item.formatDirectors)
        holder.setText(R.id.textCasts, item.formatCasts)
    }
}