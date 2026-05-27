package com.nuvio.app.features.iptv.data

object M3uParser {

    fun parse(content: String, playlistId: Long): List<IptvChannel> {
        val lines = content.lines()
        val channels = mutableListOf<IptvChannel>()
        var attrs = mutableMapOf<String, String>()

        for (line in lines) {
            when {
                line.startsWith("#EXTINF") -> {
                    attrs = parseAttrs(line)
                }
                line.startsWith("http") || line.startsWith("rtmp") -> {
                    channels.add(
                        IptvChannel(
                            playlistId = playlistId,
                            name = attrs["tvg-name"] ?: attrs["name"] ?: "Unknown",
                            streamUrl = line.trim(),
                            logoUrl = attrs["tvg-logo"],
                            group = attrs["group-title"],
                            tvgId = attrs["tvg-id"]
                        )
                    )
                    attrs = mutableMapOf()
                }
            }
        }
        return channels
    }

    private fun parseAttrs(line: String): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        val regex = Regex("""([\w-]+)="([^"]*?)"""")
        regex.findAll(line).forEach { map[it.groupValues[1]] = it.groupValues[2] }
        map["name"] = line.substringAfterLast(",").trim()
        return map
    }
}
