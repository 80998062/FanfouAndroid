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

package sinyuk.com.domain.entity

import com.sun.istack.internal.NotNull
import sinyuk.com.domain.Source
import sinyuk.com.domain.Sourced
import java.util.*

/**
 * Created by sinyuk on 2018/5/15.
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
/**
 * 应该需要展示出来的字段有
 * id
 * screenName
 * text
 * createdAt
 * avatar
 * source
 * repostId
 * repostCount
 * repostScreenName
 * liked
 * likeCount
 * commentCount
 * protected
 * photo
 */
class Feed(override var source: Source = Source.Unknown) : Sourced {
    @NotNull
    var id: String = ""
    var screenName: String? = null
    var text: String? = null
    var createdAt: Date? = null
    var avatar: Photo? = null
    var client: String? = null
    var liked: Boolean = false
    var protected: Boolean = false
    var likedCount: Int = 0
    var commentCount: Int = 0
    var repostCount: Int = 0
    var repostFeedId: String? = null
    var repostUserId: String? = null
    var repostUserName: String? = null

    var photos: MutableList<Photo>? = null
}