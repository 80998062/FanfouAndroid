package sinyuk.com.fanfou.domain.api

import java.util.*

/**
 * Created by sinyuk on 2018/4/27.
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
class AccessToken
constructor(var uniqueId: String? = null, var updatedAt: Date? = null) {
    lateinit var token: String
    lateinit var secret: String

    companion object {
        fun parseResponse(text: String): AccessToken? {
            if (!text.startsWith("oauth_token", 0)) return null
            val splits = text.split("&")
            if (splits.size != 2) return null
            val tokenPattern = splits[0].split("=")
            if (tokenPattern.size != 2) return null
            val secretPattern = splits[1].split("=")
            if (secretPattern.size != 2) return null
            return AccessToken().apply {
                token = tokenPattern[1]
                secret = secretPattern[1]
                updatedAt = Date(System.currentTimeMillis())
            }
        }
    }
}

