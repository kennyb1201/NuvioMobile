package com.nuvio.app.features.iptv.data

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request

class IptvRepository(context: Context) {

    private val dao = IptvDatabase.get(context).iptvDao()
    private val http = OkHttpClient()

    val playlists = dao.getAllPlaylists()

    fun getChannels(playlistId: Long) = dao.getChannels(playlistId)

    suspend fun addPlaylist(name: String, url: String, epgUrl: String?) {
        val playlist = IptvPlaylist(name = name, url = url, epgUrl = epgUrl)
        val id = dao.insertPlaylist(playlist)
        refreshPlaylist(id, url)
        epgUrl?.let { refreshEpg(it) }
    }

    suspend fun deletePlaylist(playlist: IptvPlaylist) {
        dao.deleteChannelsForPlaylist(playlist.id)
        dao.deletePlaylist(playlist)
    }

    suspend fun refreshPlaylist(playlistId: Long, url: String) {
        val content = fetch(url) ?: return
        val channels = M3uParser.parse(content, playlistId)
        dao.deleteChannelsForPlaylist(playlistId)
        dao.insertChannels(channels)
    }

    suspend fun refreshEpg(epgUrl: String) {
        val stream = fetchStream(epgUrl) ?: return
        val programs = EpgParser.parse(stream)
        dao.clearEpg()
        dao.insertPrograms(programs.values.flatten())
    }

    suspend fun getUpcomingPrograms(tvgId: String) =
        dao.getUpcomingPrograms(tvgId, System.currentTimeMillis())

    private fun fetch(url: String): String? = try {
        http.newCall(Request.Builder().url(url).build())
            .execute().body?.string()
    } catch (e: Exception) { null }

    private fun fetchStream(url: String) = try {
        http.newCall(Request.Builder().url(url).build())
            .execute().body?.byteStream()
    } catch (e: Exception) { null }
}
