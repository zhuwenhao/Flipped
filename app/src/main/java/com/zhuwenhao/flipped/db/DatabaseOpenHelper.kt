package com.zhuwenhao.flipped.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {

    companion object {
        private var instance: DatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseOpenHelper {
            if (instance == null) {
                instance = DatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }

        const val DB_NAME = "flipped"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(BandwagonTable.TABLE_NAME, true,
                BandwagonTable.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                BandwagonTable.TITLE to TEXT,
                BandwagonTable.VE_ID to TEXT,
                BandwagonTable.API_KEY to TEXT,
                BandwagonTable.NODE_LOCATION to TEXT,
                BandwagonTable.OS to TEXT,
                BandwagonTable.IP_ADDRESSES to TEXT,
                BandwagonTable.USER_ORDER to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}