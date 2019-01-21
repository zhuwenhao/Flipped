package com.zhuwenhao.flipped.movie

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhuwenhao.flipped.GlideApp
import com.zhuwenhao.flipped.R

class Top250Adapter : BaseQuickAdapter<Subject, BaseViewHolder>(R.layout.item_movie_subject) {

    override fun convert(helper: BaseViewHolder, item: Subject) {
        GlideApp.with(mContext).load(item.images.large).into(helper.getView(R.id.imgCover))

        helper.setText(R.id.textTitle, "${helper.adapterPosition + 1}. ${item.title}")
        if (item.rating.average == 0.0) {
            helper.setText(R.id.textDouBanRating, R.string.no_rating)
            helper.setGone(R.id.textRating, false)
        } else {
            helper.setText(R.id.textDouBanRating, R.string.dou_ban_rating)
            helper.setGone(R.id.textRating, true)
            helper.setText(R.id.textRating, item.rating.average.toString())
        }
        helper.setText(R.id.textYearAndGenres, item.yearAndGenres)
        helper.setText(R.id.textDirectors, item.formatDirectors)
        helper.setText(R.id.textCasts, item.formatCasts)
    }
}