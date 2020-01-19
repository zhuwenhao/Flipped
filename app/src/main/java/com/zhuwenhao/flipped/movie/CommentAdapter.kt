package com.zhuwenhao.flipped.movie

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zhuwenhao.flipped.GlideApp
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.view.CropCircleTransformation

class CommentAdapter : BaseQuickAdapter<MovieDetail.Comments, BaseViewHolder>(R.layout.item_movie_comment) {

    override fun convert(helper: BaseViewHolder, item: MovieDetail.Comments) {
        GlideApp.with(mContext).load(item.author.avatar).transform(CropCircleTransformation()).into(helper.getView(R.id.imgAvatar))
        helper.setText(R.id.textName, item.author.name)
        helper.setRating(R.id.ratingBar, item.rating.value.toFloat())
        helper.setText(R.id.textUsefulCount, item.usefulCount)
        helper.setText(R.id.textComment, item.content)
    }
}