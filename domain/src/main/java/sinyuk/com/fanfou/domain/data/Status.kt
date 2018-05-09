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
import android.arch.persistence.room.ForeignKey.CASCADE
import android.support.annotation.NonNull
import android.support.annotation.WorkerThread
import com.google.gson.annotations.SerializedName
import sinyuk.com.fanfou.domain.room.DateConverter
import sinyuk.com.fanfou.domain.room.dao.PlayerDao
import sinyuk.com.fanfou.domain.room.dao.StatusDao
import java.util.*

/**
 * Created by sinyuk on 2017/3/28.
 *
 */

@Entity(tableName = "statuses",
        primaryKeys = ["id"],
        indices = [(Index("uniqueId", "id"))],
        foreignKeys = [ForeignKey(entity = Player::class,
                parentColumns = ["uniqueId"],
                childColumns = ["uniqueId"],
                onDelete = ForeignKey.SET_NULL,
                onUpdate = CASCADE)])
@TypeConverters(DateConverter::class)
data class Status @JvmOverloads constructor(
        @NonNull @SerializedName("id") var id: String = "",
        var uniqueId: String? = null,
        @SerializedName("text") var text: String? = null,
        @SerializedName("source") var source: String? = null,
        @SerializedName("location") var location: String? = null,
        @Ignore @SerializedName("user") var player: Player? = null,
        @Embedded var author: Author? = null,
        @SerializedName("created_at") var createdAt: Date? = null,
        @SerializedName("favorited") var favorited: Boolean = false,
        @Embedded @SerializedName("photo") var photos: Photos? = null
        ) {
    @TypeConverters(DateConverter::class)
    data class Author @JvmOverloads constructor(var screenName: String? = "",
                                                var profileImageUrl: String? = "",
                                                var birthday: Date? = null)
}


@Suppress("unused")
@WorkerThread
fun saveStatusesAndPlayers(statusDao: StatusDao, playerDao: PlayerDao, vararg statuses: Status): Int {
    var count = 0
    for (status in statuses) {
        status.player?.let { playerDao.insert(it) } // Insert will abort if player exits
        status.uniqueId = status.player?.uniqueId
        count += if (statusDao.insert(status) >= 0) 1 else 0
    }
    return count
}