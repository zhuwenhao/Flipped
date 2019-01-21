package com.zhuwenhao.flipped.movie

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhuwenhao.flipped.GlideApp
import com.zhuwenhao.flipped.R

class RankingAdapter : BaseQuickAdapter<Ranking.RankingSubject, BaseViewHolder>(R.layout.item_movie_subject) {

    override fun convert(helper: BaseViewHolder, item: Ranking.RankingSubject) {
        GlideApp.with(mContext).load(item.subject.images.large).into(helper.getView(R.id.imgCover))

        helper.setText(R.id.textTitle, "${item.rank}. ${item.subject.title}")
        if (item.subject.rating.average == 0.0) {
            helper.setText(R.id.textDouBanRating, R.string.no_rating)
            helper.setGone(R.id.textRating, false)
        } else {
            helper.setText(R.id.textDouBanRating, R.string.dou_ban_rating)
            helper.setGone(R.id.textRating, true)
            helper.setText(R.id.textRating, item.subject.rating.average.toString())
        }
        helper.setText(R.id.textYearAndGenres, item.subject.yearAndGenres)
        helper.setText(R.id.textDirectors, item.subject.formatDirectors)
        helper.setText(R.id.textCasts, item.subject.formatCasts)
    }
}