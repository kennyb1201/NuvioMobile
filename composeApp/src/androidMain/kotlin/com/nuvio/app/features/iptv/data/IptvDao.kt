package com.nuvio.app.features.iptv.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IptvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: IptvPlaylist): Long

    @Delete
    suspend fun deletePlaylist(playlist: IptvPlaylist)

    @Query("SELECT * FROM iptv_playlists")
    fun getAllPlaylists(): Flow<List<IptvPlaylist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<IptvChannel>)

    @Query("SELECT * FROM iptv_channels WHERE playlistId = :playlistId")
    fun getChannels(playlistId: Long): Flow<List<IptvChannel>>

    @Query("DELETE FROM iptv_channels WHERE playlistId = :playlistId")
    suspend fun deleteChannelsForPlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<EpgProgram>)

    @Query("SELECT * FROM epg_programs WHERE tvgId = :tvgId AND stopMs > :nowMs ORDER BY startMs ASC LIMIT 3")
    suspend fun getUpcomingPrograms(tvgId: String, nowMs: Long): List<EpgProgram>

    @Query("DELETE FROM epg_programs")
    suspend fun clearEpg()
}
