package com.zhuwenhao.flipped.extension

import android.content.Context
import com.zhuwenhao.flipped.db.DatabaseOpenHelper

val Context.database: DatabaseOpenHelper
    get() = DatabaseOpenHelper.getInstance(applicationContext)