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

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.util.*


/**
 * Created by sinyuk on 2017/11/27.
 *
 */
@RealmClass
open class Player @JvmOverloads constructor(
        /**
         * The string representation of the unique identifier for this User. Implementations should use
         * this rather than the large, possibly un-consumable integer in id
         */
        @SerializedName(value = "id", alternate = ["id_str"])
        @PrimaryKey @Required var id: String = "",
        /**
         * The name of the user, as they've defined it. Not necessarily a person's name. Typically
         * capped at 20 characters, but subject to change.
         */
        @SerializedName("name")
        @Index var name: String = "",
        /**
         * The screen name, handle, or alias that this user identifies themselves with. screen_names are
         * unique but subject to change. Use id_str as a user identifier whenever possible. Typically a
         * maximum of 15 characters long, but some historical accounts may exist with longer names.
         */
        @SerializedName("screen_name")
        @Required @Index var screenName: String = "",
        /**
         * Nullable. The user-defined location for this account's profile. Not necessarily a location
         * nor parseable. This field will occasionally be fuzzily interpreted by the Search service.
         */
        @SerializedName("location")
        var location: String? = null,
        /**
         * Nullable. The user-defined UTF-8 string describing their account.
         */
        @SerializedName("description")
        var description: String? = null,
        /**
         * Nullable. The logged in user email address if available. Must have permission to access email
         * address.
         */
        @SerializedName("email")
        var email: String? = null,
        /**
         * URL pointing to the user's avatar image. See User Profile Images and Banners.
         */
        @SerializedName(value = "profile_image_url_large", alternate = ["profile_image_url_https"])
        var profileImageUrl: String? = null,
        /**
         * Nullable. The offset from GMT/UTC in seconds.
         */
        @SerializedName("utc_offset")
        var utcOffset: Int = 0,
        /**
         * Nullable. A URL provided by the user in association with their profile.
         */
        @SerializedName("url")
        var url: String? = null,
        /**
         * When true, indicates that this user has chosen to protect their Tweets. See About Public and
         * Protected Tweets.
         */
        @SerializedName("protected")
        var protectedUser: Boolean? = true,
        @SerializedName("followers_count")
        var followersCount: Int = 0,
        @SerializedName("friends_count")
        var friendsCount: Int = 0,
        @SerializedName("favourites_count")
        var favouritesCount: Int = 0,
        /**
         * The number of tweets (including retweets) issued by the user.
         */
        @SerializedName("statuses_count")
        var statusesCount: Int = 0,
        /**
         * 当前登录用户是否已对该用户发出关注请求
         */
        @SerializedName(value = "follow_request_sent")
        var followRequestSent: Boolean? = false,
        /**
         * 当前登录用户是否已对该用户发出关注请求
         */
        @SerializedName(value = "notifications")
        var notifications: Boolean? = false,
        /**
         * 该用户是被当前登录用户关注
         */
        @SerializedName(value = "following")
        var following: Boolean? = false,
        /**
         * The UTC datetime that the user account was created on Twitter.
         */
        @SerializedName("created_at")
        var createdAt: Date? = null,
        /**
         * The hexadecimal color chosen by the user for their background.
         */
        @SerializedName("profile_background_color")
        var profileBackgroundColor: String? = null,
        /**
         * The hexadecimal color the user has chosen to display text with in their Twitter UI.
         */
        @SerializedName("profile_text_color")
        var profileTextColor: String? = null,
        /**
         * The hexadecimal color the user has chosen to display links with in their Twitter UI.
         */
        @SerializedName("profile_link_color")
        var profileLinkColor: String? = null,
        /**
         * The hexadecimal color the user has chosen to display sidebar backgrounds with in their
         * Twitter UI.
         */
        @SerializedName("profile_sidebar_fill_color")
        var profileSidebarFillColor: String? = null,
        /**
         * The hexadecimal color the user has chosen to display sidebar borders with in their Twitter
         * UI.
         */
        @SerializedName("profile_sidebar_border_color")
        var profileSidebarBorderColor: String? = null,
        /**
         * When true, indicates the user wants their uploaded background image to be used.
         */
        @SerializedName("profile_background_image_url")
        var profileBackgroundImageUrl: String? = null,
        @SerializedName("profile_background_tile")
        var profileBackgroundTile: Boolean? = true,
        /**
         * Nullable. If possible, the user's most recent tweet or retweet.
         */
//        @SerializedName("status")
//        @Ignore var status: Status?,
        var source: Int? = -1
) : RealmObject()