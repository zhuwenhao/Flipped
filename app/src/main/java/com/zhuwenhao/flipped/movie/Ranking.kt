package com.zhuwenhao.flipped.movie

data class Ranking(val subjects: List<RankingSubject>,
                   val title: String) {

    data class RankingSubject(val subject: Subject,
                              val rank: Int)
}