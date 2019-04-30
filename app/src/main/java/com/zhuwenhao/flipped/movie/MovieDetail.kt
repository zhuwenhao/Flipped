package com.zhuwenhao.flipped.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieDetail(val rating: Rating,
                       @SerializedName("original_title") val originalTitle: String,
                       val images: Image,
                       val year: String,
                       val title: String,
                       val languages: List<String>,
                       @SerializedName("pubdates") val pubDates: List<String>,
                       val durations: List<String>,
                       val genres: List<String>,
                       val trailers: List<Trailer>,
                       val casts: List<Person>,
                       val countries: List<String>,
                       @SerializedName("mainland_pubdate") val mainlandPubDate: String,
                       val photos: List<Photo>,
                       val summary: String,
                       val directors: List<Person>,
                       @SerializedName("ratings_count") val ratingsCount: Int,
                       val aka: List<String>) : Serializable {

    data class Rating(val max: Int,
                      val average: Float,
                      val details: Detail,
                      val stars: String,
                      val min: Int) : Serializable {

        data class Detail(@SerializedName("1") val v1: Int,
                          @SerializedName("2") val v2: Int,
                          @SerializedName("3") val v3: Int,
                          @SerializedName("4") val v4: Int,
                          @SerializedName("5") val v5: Int) : Serializable
    }

    data class Image(val small: String,
                     val large: String,
                     val medium: String) : Serializable

    data class Trailer(val medium: String,
                       val title: String,
                       @SerializedName("subject_id") val subjectId: String,
                       val alt: String,
                       val small: String,
                       @SerializedName("resource_url") val resourceUrl: String,
                       val id: String) : Serializable

    data class Person(val avatars: Image,
                      @SerializedName("name_en") val nameEn: String,
                      val name: String,
                      val alt: String,
                      val id: String) : Serializable

    data class Photo(val thumb: String,
                     val image: String,
                     val cover: String,
                     val alt: String,
                     val id: String,
                     val icon: String) : Serializable
}