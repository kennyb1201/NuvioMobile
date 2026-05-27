package com.nuvio.app.features.iptv.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun IptvScreen(
    onChannelClick: (streamUrl: String, channelName: String) -> Unit = { _, _ -> },
    viewModel: IptvViewModel = viewModel()
) {
    val playlists by viewModel.playlists.observeAsState(emptyList())
    val channels by viewModel.channels.observeAsState(emptyList())
    val showAddDialog by viewModel.showAddDialog.observeAsState(false)
    var selectedPlaylistId by remember { mutableStateOf<Long?>(null) }

    if (showAddDialog) {
        AddPlaylistDialog(
            onDismiss = { viewModel.hideAddPlaylistDialog() },
            onConfirm = { name, url, epgUrl ->
                viewModel.addPlaylist(name, url, epgUrl)
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "IPTV",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.showAddPlaylistDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Playlist")
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Left panel - playlists
            LazyColumn(
                modifier = Modifier
                    .width(180.dp)
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                item {
                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(items = playlists, key = { it.id }) { playlist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPlaylistId = playlist.id
                                viewModel.selectPlaylist(playlist.id)
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = playlist.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.deletePlaylist(playlist) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier
                .fillMaxHeight()
                .width(1.dp))

            // Right panel - channels
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                item {
                    Text(
                        text = "Channels",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(items = channels, key = { it.id }) { channel ->
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChannelClick(channel.streamUrl, channel.name) }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddPlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, url: String, epgUrl: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var epgUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add M3U Playlist") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Playlist Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("M3U URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = epgUrl,
                    onValueChange = { epgUrl = it },
                    label = { Text("EPG URL (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && url.isNotBlank()) {
                        onConfirm(name, url, epgUrl.ifBlank { null })
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
