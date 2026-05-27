package com.nuvio.app.features.iptv.data

import android.util.Xml
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

object EpgParser {

    fun parse(inputStream: InputStream): Map<String, List<EpgProgram>> {
        val result = mutableMapOf<String, MutableList<EpgProgram>>()
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, null)

        var tvgId = ""
        var title = ""
        var desc = ""
        var start = 0L
        var stop = 0L
        val fmt = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US)

        var eventType = parser.eventType
        while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
            when {
                eventType == org.xmlpull.v1.XmlPullParser.START_TAG && parser.name == "programme" -> {
                    tvgId = parser.getAttributeValue(null, "channel") ?: ""
                    start = fmt.parse(parser.getAttributeValue(null, "start") ?: "")?.time ?: 0L
                    stop = fmt.parse(parser.getAttributeValue(null, "stop") ?: "")?.time ?: 0L
                    title = ""; desc = ""
                }
                eventType == org.xmlpull.v1.XmlPullParser.START_TAG && parser.name == "title" -> {
                    title = parser.nextText()
                }
                eventType == org.xmlpull.v1.XmlPullParser.START_TAG && parser.name == "desc" -> {
                    desc = parser.nextText()
                }
                eventType == org.xmlpull.v1.XmlPullParser.END_TAG && parser.name == "programme" -> {
                    if (tvgId.isNotEmpty()) {
                        result.getOrPut(tvgId) { mutableListOf() }.add(
                            EpgProgram(
                                tvgId = tvgId,
                                title = title,
                                description = desc,
                                startMs = start,
                                stopMs = stop
                            )
                        )
                    }
                }
            }
            eventType = parser.next()
        }
        return result
    }
}
