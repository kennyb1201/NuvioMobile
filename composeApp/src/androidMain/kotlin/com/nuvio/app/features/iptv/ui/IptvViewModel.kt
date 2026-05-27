package com.nuvio.app.features.iptv.ui

import android.app.Application
import androidx.lifecycle.*
import com.nuvio.app.features.iptv.data.IptvPlaylist
import com.nuvio.app.features.iptv.data.IptvRepository
import kotlinx.coroutines.launch

class IptvViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = IptvRepository(app)
    val playlists = repo.playlists.asLiveData()

    private val _selectedPlaylistId = MutableLiveData<Long>()
    val channels = _selectedPlaylistId.switchMap {
        repo.getChannels(it).asLiveData()
    }

    private val _showAddDialog = MutableLiveData(false)
    val showAddDialog: LiveData<Boolean> = _showAddDialog

    fun selectPlaylist(id: Long) { _selectedPlaylistId.value = id }

    fun showAddPlaylistDialog() { _showAddDialog.value = true }
    fun hideAddPlaylistDialog() { _showAddDialog.value = false }

    fun addPlaylist(name: String, url: String, epgUrl: String?) {
        viewModelScope.launch {
            repo.addPlaylist(name, url, epgUrl)
            _showAddDialog.value = false
        }
    }

    fun deletePlaylist(playlist: IptvPlaylist) {
        viewModelScope.launch { repo.deletePlaylist(playlist) }
    }

    fun refreshEpg(epgUrl: String) {
        viewModelScope.launch { repo.refreshEpg(epgUrl) }
    }
}
