package sinyuk.com.fanfou.domain.api

import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import sinyuk.com.fanfou.domain.data.Photos
import sinyuk.com.fanfou.domain.data.Status
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sinyuk on 2018/4/23.
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│        _______. __  .__   __. ____    ____  __    __   __  ___   │
│       /       ||  | |  \ |  | \   \  /   / |  |  |  | |  |/  /   │
│      |   (----`|  | |   \|  |  \   \/   /  |  |  |  | |  '  /    │
│       \   \    |  | |  . `  |   \_    _/   |  |  |  | |    <     │
│   .----)   |   |  | |  |\   |     |  |     |  `--'  | |  .  \    │
│   |_______/    |__| |__| \__|     |__|      \______/  |__|\__\   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
 */
const val CREATED_AT_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy"
const val BIRTHDAY_FORMAT = "yyyy-MM-dd"

val formatters = mutableListOf(
        SimpleDateFormat(CREATED_AT_FORMAT, Locale.ENGLISH),
        SimpleDateFormat(BIRTHDAY_FORMAT, Locale.ENGLISH))

val DateDeserializer = JsonDeserializer<Date> { json, _, _ ->
    if (json.isJsonNull || json.asString.isEmpty()) {
        Date(System.currentTimeMillis())
    } else {
        var date: Date? = null
        for (formatter in formatters) {
            try {
                formatter.timeZone = TimeZone.getDefault()
                date = formatter.parse(json.asString)
                break
            } catch (e: ParseException) {
                // no-op
            }
        }

        if (date == null)
            throw  JsonParseException("Unparseable date: \"${json.asString}\". ")
        else
            return@JsonDeserializer date
    }
}


@Deprecated("废物")
val StatusDeserializer = JsonDeserializer<Status> { json, _, _ ->
    val status = Status()
    json?.asJsonObject?.apply {
        get("user")?.asJsonObject.apply {
            status.uniqueId = get("unique_id").asString
        }
        status.id = get("id").asString
        status.text = get("text").asString
        status.source = get("source").asString
        status.location = get("location").asString
        formatters[0].timeZone = TimeZone.getDefault()
        status.createdAt = formatters[0].parse(get("createdAt").asString)
        status.favorited = get("favorited").asBoolean

        get("photo")?.asJsonObject.apply {
            val photos = Photos(url = get("url").asString,
                    imageurl = get("imageurl").asString,
                    thumburl = get("thumburl").asString,
                    largeurl = get("largeurl").asString)
            status.photos = photos
        }
    }
    status
}