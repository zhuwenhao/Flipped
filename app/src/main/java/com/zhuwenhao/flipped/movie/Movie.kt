package com.zhuwenhao.flipped.movie

data class Movie(val count: Int,
                 val start: Int,
                 val total: Int,
                 val subjects: List<Subject>,
                 val title: String)