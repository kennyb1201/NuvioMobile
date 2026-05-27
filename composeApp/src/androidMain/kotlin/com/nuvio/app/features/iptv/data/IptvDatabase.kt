package com.nuvio.app.features.iptv.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [IptvPlaylist::class, IptvChannel::class, EpgProgram::class],
    version = 1,
    exportSchema = false
)
abstract class IptvDatabase : RoomDatabase() {
    abstract fun iptvDao(): IptvDao

    companion object {
        @Volatile private var INSTANCE: IptvDatabase? = null

        fun get(context: Context): IptvDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, IptvDatabase::class.java, "iptv_db")
                    .build().also { INSTANCE = it }
            }
    }
}
