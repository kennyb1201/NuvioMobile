package com.nuvio.app.features.iptv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "iptv_playlists")
data class IptvPlaylist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val epgUrl: String? = null
)

@Entity(tableName = "iptv_channels")
data class IptvChannel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val name: String,
    val streamUrl: String,
    val logoUrl: String?,
    val group: String?,
    val tvgId: String?
)

@Entity(tableName = "epg_programs")
data class EpgProgram(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tvgId: String,
    val title: String,
    val description: String?,
    val startMs: Long,
    val stopMs: Long
)
