package com.zhuwenhao.flipped.movie.entity

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.annotations.SerializedName
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
                   val id: String) : MultiItemEntity {

    companion object {
        const val TYPE_HEADER: Int = 1
        const val TYPE_DATA: Int = 2
    }

    var type: Int = 0

    override fun getItemType(): Int {
        return type
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
            subject.id) {
        type = itemType
        this.mainlandDate = mainlandDate
        this.mainlandDateTime = mainlandDateTime
        this.mainlandDateForHeader = mainlandDateForHeader
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