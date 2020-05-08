package com.zhuwenhao.flipped.movie

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil
import com.zhuwenhao.flipped.R

class ComingSoonAdapter(data: MutableList<Subject>) : BaseMultiItemQuickAdapter<Subject, BaseViewHolder>(data), LoadMoreModule {

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

    override fun convert(holder: BaseViewHolder, item: Subject) {
        when (holder.itemViewType) {
            Subject.TYPE_HEADER -> {
                holder.setText(R.id.textHeader, item.mainlandDateForHeader)
            }
            Subject.TYPE_DATA -> {
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
                holder.setText(R.id.textYearAndGenres, item.yearAndGenres)
                holder.setText(R.id.textDirectors, item.formatDirectors)
                holder.setText(R.id.textCasts, item.formatCasts)
            }
        }
    }
}