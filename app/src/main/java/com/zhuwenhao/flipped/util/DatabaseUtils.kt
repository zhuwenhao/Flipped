package com.zhuwenhao.flipped.util

import android.content.Context
import com.zhuwenhao.flipped.bandwagon.entity.Bandwagon
import com.zhuwenhao.flipped.db.BandwagonTable
import com.zhuwenhao.flipped.extension.database
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update

/**
 * TODO 暂时先用这个，以后再改
 */
class DatabaseUtils {

    companion object {
        fun getBandwagonList(context: Context): List<Bandwagon> {
            return context.database.use {
                val builder = select(BandwagonTable.TABLE_NAME, BandwagonTable.ID, BandwagonTable.TITLE, BandwagonTable.VE_ID, BandwagonTable.API_KEY,
                        BandwagonTable.NODE_LOCATION, BandwagonTable.OS, BandwagonTable.IP_ADDRESSES, BandwagonTable.USER_ORDER)
                builder.parseList(classParser())
            }
        }

        fun addBandwagon(context: Context, title: String, veId: String, apiKey: String) {
            context.database.use {
                insert(BandwagonTable.TABLE_NAME,
                        BandwagonTable.TITLE to title,
                        BandwagonTable.VE_ID to veId,
                        BandwagonTable.API_KEY to apiKey,
                        BandwagonTable.USER_ORDER to getBandwagonLastUserOrder(context))
            }
        }

        private fun getBandwagonLastUserOrder(context: Context): Int {
            var lastUserOrder = 0
            context.database.use {
                select(BandwagonTable.TABLE_NAME, "MAX(${BandwagonTable.USER_ORDER}) as lastUserOrder").exec {
                    moveToNext()
                    lastUserOrder = getInt(getColumnIndex("lastUserOrder")) + 1
                }
            }
            return lastUserOrder
        }

        fun updateBandwagon(context: Context, id: Int, title: String, veId: String, apiKey: String) {
            context.database.use {
                update(BandwagonTable.TABLE_NAME, BandwagonTable.TITLE to title, BandwagonTable.VE_ID to veId, BandwagonTable.API_KEY to apiKey)
                        .whereArgs("id = {bId}", "bId" to id)
                        .exec()
            }
        }
    }
}