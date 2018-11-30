package com.zhuwenhao.flipped.bandwagon.entity

import java.io.Serializable

data class Bandwagon(val id: Int,
                     val title: String,
                     val veId: String,
                     val apiKey: String,
                     val nodeLocation: String?,
                     val os: String?,
                     val ipAddresses: String?,
                     val userOrder: Int) : Serializable