package com.zhuwenhao.flipped.util

import android.content.Context
import android.util.TypedValue

class DisplayUtils {

    companion object {

        /**
         * dp to px
         *
         * @param context context
         * @param dpValue dp
         * @return px
         */
        fun dpToPx(context: Context, dpValue: Float): Int {
            return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics) + 0.5F).toInt()
        }

        /**
         * sp to px
         *
         * @param context context
         * @param spValue sp
         * @return px
         */
        fun spToPx(context: Context, spValue: Float): Int {
            return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.resources.displayMetrics) + 0.5F).toInt()
        }
    }
}