package sinyuk.com.fanfou.domain.api

import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
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