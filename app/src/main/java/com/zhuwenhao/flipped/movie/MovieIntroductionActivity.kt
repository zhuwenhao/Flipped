package com.zhuwenhao.flipped.movie

import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import kotlinx.android.synthetic.main.activity_movie_introduction.*

class MovieIntroductionActivity : BaseSubActivity() {

    override fun provideLayoutId(): Int {
        return R.layout.activity_movie_introduction
    }

    override fun initView() {
        setSupportActionBar(toolbar)
    }

    override fun initData() {
        val movie = intent.getSerializableExtra("movie") as MovieDetail

        supportActionBar?.title = movie.title

        textSummary.text = movie.summary

        textDirectors.text = movie.directors.joinToString(separator = "、", transform = {
            it.name
        })
        textCasts.text = movie.casts.joinToString(separator = "、", transform = {
            it.name
        })

        textOriginalTitle.text = movie.originalTitle
        textGenres.text = movie.genres.joinToString(separator = " / ")
        textCountries.text = movie.countries.joinToString(separator = " / ")
        textLanguages.text = movie.languages.joinToString(separator = " / ")
        textPubDates.text = movie.pubDates.joinToString(separator = " / ")
        textDurations.text = movie.durations.joinToString(separator = " / ")
        textAka.text = movie.aka.joinToString(separator = " / ")
    }

    override fun isSwipeBackEdgeOnly() = false
}