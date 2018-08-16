package com.zhuwenhao.flipped.movie

import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.movie.entity.Movie
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface DouBanMovieApi {

    @GET("in_theaters?apikey=" + Constants.DOU_BAN_API_KEY)
    fun getInTheaters(@Query("city") city: String, @Query("start") start: Int, @Query("count") count: Int): Observable<Movie>

    @GET("coming_soon?apikey=" + Constants.DOU_BAN_API_KEY)
    fun getComingSoon(@Query("city") city: String, @Query("start") start: Int, @Query("count") count: Int): Observable<Movie>
}