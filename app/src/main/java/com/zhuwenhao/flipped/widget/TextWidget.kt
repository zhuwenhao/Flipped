package com.zhuwenhao.flipped.widget

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class TextWidget(@Id var id: Long = 0,
                      val widgetId: Int,
                      var title: String,
                      var titleSize: String,
                      var titleColor: String,
                      var textAlignment: Int)