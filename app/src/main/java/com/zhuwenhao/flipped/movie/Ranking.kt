package com.zhuwenhao.flipped.movie

data class Ranking(val subjects: MutableList<RankingSubject>,
                   val title: String) {

    data class RankingSubject(val subject: Subject,
                              val rank: Int)
}