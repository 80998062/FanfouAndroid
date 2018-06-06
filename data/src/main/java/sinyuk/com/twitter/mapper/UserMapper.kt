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

package sinyuk.com.twitter.mapper

import com.twitter.sdk.android.core.models.User
import sinyuk.com.common.Source
import sinyuk.com.common.realm.model.Player
import java.util.*

/**
 * Created by sinyuk on 2018/5/30.
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
object UserMapper : Mapper<User, Player> {
    override fun transform(k: User): Player? = Player(source = Source.Twitter)
            .apply {
                uniqueId = k.idStr
                id = k.name
                screenName = k.screenName
                location = k.location
                gender = null
                birthday = Date()
                description = k.description
                profileImageUrl = k.profileImageUrlHttps
                profileImageUrlLarge = k.profileImageUrlHttps
                url = k.url
                followersCount = k.followersCount
                friendsCount = k.friendsCount
                statusesCount = k.statusesCount
                photoCount = 0 //
                following = null //
                notifications = null //
                createdAt = null //
                profileBackgroundImageUrl = k.profileBackgroundImageUrlHttps
            }

    override fun transform(k: Collection<User>): Collection<Player>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}