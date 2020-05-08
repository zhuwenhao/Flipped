package com.zhuwenhao.flipped.movie

data class Movie(val count: Int,
                 val start: Int,
                 val total: Int,
                 val subjects: MutableList<Subject>,
                 val title: String)