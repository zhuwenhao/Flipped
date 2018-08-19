package com.zhuwenhao.flipped.movie.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.movie.entity.Subject
import com.zhuwenhao.flipped.util.StringUtils
import org.joda.time.DateTime

class InTheatersAdapter : BaseQuickAdapter<Subject, BaseViewHolder>(R.layout.item_in_theaters) {

    override fun convert(helper: BaseViewHolder, item: Subject) {
        Glide.with(mContext).load(item.images.large).into(helper.getView(R.id.imgCover))

        helper.setText(R.id.textTitle, item.title)
        if (item.rating.average == 0.0) {
            helper.setText(R.id.textDouBanRating, R.string.no_rating)
            helper.setGone(R.id.textRating, false)
        } else {
            helper.setText(R.id.textDouBanRating, R.string.dou_ban_rating)
            helper.setGone(R.id.textRating, true)
            helper.setText(R.id.textRating, item.rating.average.toString())
        }
        if (DateTime.now().millis < item.mainlandDateTime.millis) {
            helper.setGone(R.id.textDouBanRating, item.rating.average != 0.0)
            helper.setText(R.id.textMainlandPubDate, StringUtils.formatMainlandPubDate(item.mainlandDateTime))
            helper.setGone(R.id.textMainlandPubDate, true)
        } else {
            helper.setGone(R.id.textDouBanRating, true)
            helper.setGone(R.id.textMainlandPubDate, false)
        }
        helper.setText(R.id.textYearAndGenres, StringUtils.formatYearAndGenres(item.year, item.genres))
        helper.setText(R.id.textDirectors, if (item.directors.isNotEmpty()) mContext.getString(R.string.item_directors, StringUtils.formatDirectors(item.directors)) else "")
        helper.setText(R.id.textCasts, if (item.casts.isNotEmpty()) mContext.getString(R.string.item_casts, StringUtils.formatCasts(item.casts)) else "")
    }
}