package com.zhuwenhao.flipped.movie

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhuwenhao.flipped.GlideApp
import com.zhuwenhao.flipped.R

class RankingAdapter : BaseQuickAdapter<Ranking.RankingSubject, BaseViewHolder>(R.layout.item_movie_subject) {

    override fun convert(holder: BaseViewHolder, item: Ranking.RankingSubject) {
        GlideApp.with(context).load(item.subject.images.large).into(holder.getView(R.id.imgCover))

        holder.setText(R.id.textTitle, "${item.rank}. ${item.subject.title}")
        if (item.subject.rating.average == 0.0) {
            holder.setText(R.id.textDouBanRating, R.string.no_rating)
            holder.setGone(R.id.textRating, true)
        } else {
            holder.setText(R.id.textDouBanRating, R.string.dou_ban_rating)
            holder.setGone(R.id.textRating, false)
            holder.setText(R.id.textRating, item.subject.rating.average.toString())
        }
        holder.setText(R.id.textYearAndGenres, item.subject.yearAndGenres)
        holder.setText(R.id.textDirectors, item.subject.formatDirectors)
        holder.setText(R.id.textCasts, item.subject.formatCasts)
    }
}