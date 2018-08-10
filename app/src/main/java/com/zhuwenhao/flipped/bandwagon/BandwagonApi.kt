package com.zhuwenhao.flipped.bandwagon

import com.zhuwenhao.flipped.bandwagon.entity.BandwagonInfo
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query

interface BandwagonApi {

    @POST("getLiveServiceInfo")
    fun getBandwagonInfo(@Query("veid") veId: String, @Query("api_key") apiKey: String): Observable<BandwagonInfo>
}