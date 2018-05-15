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

package sinyuk.com.common.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import sinyuk.com.fanfou.data.BuildConfig
import sinyuk.com.fanfou.dao.PlayerDao
import sinyuk.com.fanfou.dao.StatusDao
import sinyuk.com.fanfou.data.Player
import sinyuk.com.fanfou.data.Status


/**
 * Created by sinyuk on 2017/11/27.
 *
 */
@Database(entities = [(Player::class), (Status::class)],
        version = BuildConfig.VERSION_CODE, exportSchema = true)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun statusDao(): StatusDao
    abstract fun playerDao(): PlayerDao
}