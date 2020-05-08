package com.zhuwenhao.flipped.movie

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhuwenhao.flipped.GlideApp
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.view.CropCircleTransformation
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class CommentAdapter : BaseQuickAdapter<MovieDetail.Comments, BaseViewHolder>(R.layout.item_movie_comment) {

    override fun convert(holder: BaseViewHolder, item: MovieDetail.Comments) {
        GlideApp.with(context).load(item.author.avatar).transform(CropCircleTransformation()).into(holder.getView(R.id.imgAvatar))
        holder.setText(R.id.textName, item.author.name)
        holder.getView<MaterialRatingBar>(R.id.ratingBar).rating = item.rating.value.toFloat()
        holder.setText(R.id.textUsefulCount, item.usefulCount)
        holder.setText(R.id.textComment, item.content)
    }
}