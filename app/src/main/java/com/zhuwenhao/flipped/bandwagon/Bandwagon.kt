package com.zhuwenhao.flipped.bandwagon

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

@Entity
data class Bandwagon(@Id var id: Long = 0,
                     var title: String,
                     var veId: String,
                     var apiKey: String,
                     var ipAddresses: String? = null,
                     var nodeLocation: String? = null,
                     var userOrder: Int) : Serializable