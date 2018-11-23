package com.zhuwenhao.flipped.movie.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

data class City(val alphabet: String,
                val name: String) : MultiItemEntity {

    companion object {
        const val TYPE_HEADER: Int = 1
        const val TYPE_DATA: Int = 2
    }

    var isChecked: Boolean = false

    var type: Int = 0

    override fun getItemType(): Int {
        return type
    }

    constructor(itemType: Int, alphabet: String, name: String, isChecked: Boolean) : this(alphabet, name) {
        type = itemType
        this.isChecked = isChecked
    }
}