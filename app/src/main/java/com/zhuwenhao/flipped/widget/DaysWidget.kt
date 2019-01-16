package com.zhuwenhao.flipped.widget

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class DaysWidget(@Id var id: Long = 0,
                      val widgetId: Int,
                      var title: String,
                      var startDate: String,
                      var titleSize: String,
                      var titleColor: String,
                      var daysSize: String,
                      var daysColor: String,
                      var textAlignment: Int)