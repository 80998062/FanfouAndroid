/*
 *   Copyright 2081 Sinyuk
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
class FanfouAccessToken
constructor(var updatedAt: Date? = null) {
    lateinit var token: String
    lateinit var secret: String
    companion object {
        fun parseResponse(text: String): FanfouAccessToken? {
            if (!text.startsWith("oauth_token", 0)) return null
            val splits = text.split("&")
            if (splits.size != 2) return null
            val tokenPattern = splits[0].split("=")
            if (tokenPattern.size != 2) return null
            val secretPattern = splits[1].split("=")
            if (secretPattern.size != 2) return null
            return FanfouAccessToken().apply {
                token = tokenPattern[1]
                secret = secretPattern[1]
                updatedAt = Date(System.currentTimeMillis())
            }
        }
    }
}

