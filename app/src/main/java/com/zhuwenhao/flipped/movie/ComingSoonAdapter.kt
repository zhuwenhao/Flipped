package com.zhuwenhao.flipped.movie

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil
import com.zhuwenhao.flipped.R

class ComingSoonAdapter(data: List<Subject>) : BaseMultiItemQuickAdapter<Subject, BaseViewHolder>(data) {

    init {
        addItemType(Subject.TYPE_HEADER, R.layout.item_coming_soon_header)
        addItemType(Subject.TYPE_DATA, R.layout.item_movie_subject)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, Subject.TYPE_HEADER)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        FullSpanUtil.onViewAttachedToWindow(holder, this, Subject.TYPE_HEADER)
    }

    override fun convert(helper: BaseViewHolder, item: Subject) {
        when (helper.itemViewType) {
            Subject.TYPE_HEADER -> {
                helper.setText(R.id.textHeader, item.mainlandDateForHeader)
            }
            Subject.TYPE_DATA -> {
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
                helper.setText(R.id.textYearAndGenres, item.yearAndGenres)
                helper.setText(R.id.textDirectors, item.formatDirectors)
                helper.setText(R.id.textCasts, item.formatCasts)
            }
        }
    }
}