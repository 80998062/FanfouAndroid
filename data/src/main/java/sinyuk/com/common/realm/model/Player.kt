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

@file:Suppress("unused")

package sinyuk.com.common.realm.model

import io.realm.RealmObject
import io.realm.annotations.*
import sinyuk.com.common.Source
import java.util.*


/**
 * Created by sinyuk on 2017/11/27.
 *
 */
@RealmClass
open class Player @JvmOverloads constructor(
        @Required @PrimaryKey val uniqueId: String,
        @Required val idStr: String,
        @Required @Index val screenName: String,
        val location: String?,
        val gender: String? = "",
        val birthday: Date? = null,
        val description: String? = "",
        val profileImageUrl: String? = "",
        val profileImageUrlLarge: String?,
        val url: String?,
        var protectedX: Boolean? = true,
        var followersCount: Int? = 0,
        var friendsCount: Int? = 0,
        var favouritesCount: Int? = 0,
        var statusesCount: Int? = 0,
        var photoCount: Int? = 0,
        var following: Boolean? = false,
        var notifications: Boolean? = false,
        val createdAt: Date? = null,
        val profileBackgroundImageUrl: String? = "",
        @Ignore val status: Status? = null,
        val source: Source
) : RealmObject()