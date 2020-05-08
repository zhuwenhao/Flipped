package com.zhuwenhao.flipped.movie

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.annotations.SerializedName
import com.zhuwenhao.flipped.FlippedApp
import com.zhuwenhao.flipped.R
import org.joda.time.DateTime

data class Subject(val rating: Rating,
                   val genres: List<String>,
                   val title: String,
                   val casts: List<Cast>,
                   val durations: List<String>,
                   @SerializedName("collect_count") val collectCount: Int,
                   @SerializedName("mainland_pubdate") val mainlandPubDate: String,
                   @SerializedName("has_video") val hasVideo: Boolean,
                   @SerializedName("original_title") val originalTitle: String,
                   val subtype: String,
                   val directors: List<Director>,
                   @SerializedName("pubdates") val pubDates: List<String>,
                   val year: String,
                   val images: Image,
                   val alt: String,
                   val id: String,
                   override val itemType: Int) : MultiItemEntity {

    companion object {
        const val TYPE_HEADER: Int = 1
        const val TYPE_DATA: Int = 2
    }

    constructor(itemType: Int, mainlandDate: String, mainlandDateTime: DateTime, mainlandDateForHeader: String, subject: Subject) : this(subject.rating,
            subject.genres,
            subject.title,
            subject.casts,
            subject.durations,
            subject.collectCount,
            subject.mainlandPubDate,
            subject.hasVideo,
            subject.originalTitle,
            subject.subtype,
            subject.directors,
            subject.pubDates,
            subject.year,
            subject.images,
            subject.alt,
            subject.id,
            itemType) {
        this.mainlandDate = mainlandDate
        this.mainlandDateTime = mainlandDateTime
        this.mainlandDateForHeader = mainlandDateForHeader
    }

    val yearAndGenres: String
        get() {
            val sb = StringBuilder()
            sb.append(year)
            if (genres.isEmpty())
                return sb.toString()

            sb.append(" / ")
            sb.append(genres.joinToString(separator = " "))
            return sb.toString()
        }

    val formatDirectors: String
        get() {
            return if (directors.isEmpty()) {
                ""
            } else {
                FlippedApp.getInstance().getString(R.string.item_directors, directors.joinToString(separator = " ", transform = { it.name }))
            }
        }

    val formatCasts: String
        get() {
            return if (casts.isEmpty()) {
                ""
            } else {
                FlippedApp.getInstance().getString(R.string.item_casts, casts.joinToString(separator = " ", transform = { it.name }))
            }
        }

    var mainlandDate: String = ""

    var mainlandDateTime: DateTime = DateTime(1995, 11, 10, 0, 0)

    var mainlandDateForHeader: String = ""

    data class Rating(val max: Int,
                      val average: Double,
                      val details: Detail,
                      val stars: String,
                      val min: Int) {

        data class Detail(@SerializedName("1") val v1: Int,
                          @SerializedName("2") val v2: Int,
                          @SerializedName("3") val v3: Int,
                          @SerializedName("4") val v4: Int,
                          @SerializedName("5") val v5: Int)
    }

    data class Cast(val avatars: Avatar,
                    @SerializedName("name_en") val nameEn: String,
                    val name: String,
                    val alt: String,
                    val id: String) {

        data class Avatar(val small: String,
                          val large: String,
                          val medium: String)
    }

    data class Director(val avatars: Avatar,
                        @SerializedName("name_en") val nameEn: String,
                        val name: String,
                        val alt: String,
                        val id: String) {

        data class Avatar(val small: String,
                          val large: String,
                          val medium: String)
    }

    data class Image(val small: String,
                     val large: String,
                     val medium: String)
}