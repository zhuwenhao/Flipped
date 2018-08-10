package com.zhuwenhao.flipped.movie.entity

data class Movie(val count: Int,
                 val start: Int,
                 val total: Int,
                 val subjects: List<Subject>,
                 val title: String)