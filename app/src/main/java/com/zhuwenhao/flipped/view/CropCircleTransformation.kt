package com.zhuwenhao.flipped.view

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

class CropCircleTransformation : BitmapTransformation() {

    companion object {
        const val VERSION = 1
        const val ID = "com.zhuwenhao.flipped.view.CropCircleTransformation$VERSION"
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(CHARSET))
    }

    override fun toString(): String {
        return "CropCircleTransformation()"
    }

    override fun equals(other: Any?): Boolean {
        return other is CropCircleTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }
}