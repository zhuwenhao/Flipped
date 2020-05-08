package com.zhuwenhao.flipped.movie

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.util.StringUtils
import org.joda.time.DateTime

class InTheatersAdapter : BaseQuickAdapter<Subject, BaseViewHolder>(R.layout.item_movie_subject), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: Subject) {
        Glide.with(context).load(item.images.large).into(holder.getView(R.id.imgCover))

        holder.setText(R.id.textTitle, item.title)
        if (item.rating.average == 0.0) {
            holder.setText(R.id.textDouBanRating, R.string.no_rating)
            holder.setGone(R.id.textRating, true)
        } else {
            holder.setText(R.id.textDouBanRating, R.string.dou_ban_rating)
            holder.setGone(R.id.textRating, false)
            holder.setText(R.id.textRating, item.rating.average.toString())
        }
        if (DateTime.now().millis < item.mainlandDateTime.millis) {
            holder.setGone(R.id.textDouBanRating, item.rating.average == 0.0)
            holder.setText(R.id.textMainlandPubDate, StringUtils.formatMainlandPubDate(item.mainlandDateTime))
            holder.setGone(R.id.textMainlandPubDate, false)
        } else {
            holder.setGone(R.id.textDouBanRating, false)
            holder.setGone(R.id.textMainlandPubDate, true)
        }
        holder.setText(R.id.textYearAndGenres, item.yearAndGenres)
        holder.setText(R.id.textDirectors, item.formatDirectors)
        holder.setText(R.id.textCasts, item.formatCasts)
    }
}