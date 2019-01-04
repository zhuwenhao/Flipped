package com.zhuwenhao.flipped.db

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class DateWidget(@Id var id: Long = 0,
                      val widgetId: Int,
                      var title: String)