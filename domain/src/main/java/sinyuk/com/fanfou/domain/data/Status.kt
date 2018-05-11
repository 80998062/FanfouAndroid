/*
 *
 *  * Apache License
 *  *
 *  * Copyright [2017] Sinyuk
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package sinyuk.com.fanfou.domain.data

import android.arch.persistence.room.*
import android.support.annotation.NonNull
import com.google.gson.annotations.SerializedName
import sinyuk.com.fanfou.domain.room.DateConverter
import java.util.*

/**
 * Created by sinyuk on 2017/3/28.
 *
 */

@Entity(tableName = "statuses",
        primaryKeys = ["id"],
        indices = [(Index( "id"))]
//        foreignKeys = [ForeignKey(entity = Player::class,
//                parentColumns = ["uniqueId"],
//                childColumns = ["uniqueId"],
//                onDelete = CASCADE,
//                onUpdate = CASCADE)]
)
@TypeConverters(DateConverter::class)
data class Status @JvmOverloads constructor(
        @NonNull @SerializedName("id") var id: String = "",
        @NonNull var uniqueId: String = "", // 这条状态是由@{uniqueId}的人创建的
        @SerializedName("text") var text: String? = null,
        @SerializedName("source") var source: String? = null,
        @SerializedName("location") var location: String? = null,
        @Ignore @SerializedName("user") var player: Player? = null,
        @Embedded var author: Author? = null,
        @SerializedName("created_at") var createdAt: Date? = null,
        @SerializedName("favorited") var favorited: Boolean = false,
        @Embedded @SerializedName("photo") var photos: Photos? = null) {

    @TypeConverters(DateConverter::class)
    data class Author @JvmOverloads constructor(var screenName: String? = "",
                                                var profileImageUrl: String? = "",
                                                var birthday: Date? = null)


//    @Suppress("unused")
//    fun addPath(flags: Int) {
//        path = path or flags
//    }
//
//    @Suppress("unused")
//    fun removePath(flags: Int) {
//        path = path and flags.inv()
//    }

    companion object {
        /**
         * Notice: 对于收藏的状态不会被保存在数据库,
         * 因为那是many-to-many的关系,处理起来太复杂了🤦‍♀️
         */

//        const val STATUS_NO_FLAG = 0x00000000
//        const val STATUS_FLAG_HOME = 0x00000001
//        const val STATUS_FLAG_LIKED = 0x00000010
//        const val STATUS_FLAG_USER = 0x00000100 // photo belongs to user's
//
//
//        fun convert2Flag(path: String): Int = when (path) {
//            TIMELINE_HOME -> STATUS_FLAG_HOME
//            TIMELINE_USER -> STATUS_FLAG_USER
//            TIMELINE_FAVORITES -> STATUS_FLAG_LIKED
//            else -> STATUS_NO_FLAG
//        }
    }
}


