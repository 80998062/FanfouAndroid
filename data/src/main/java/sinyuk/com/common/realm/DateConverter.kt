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

package sinyuk.com.common.realm

import android.arch.persistence.room.TypeConverter
import sinyuk.com.common.Source
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?) = if (value == null) null else Date(value)


    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time
}

class SourceConverter {
    @TypeConverter
    fun fromSource(source: Source?) = when (source) {
        Source.Fanfou -> 1
        Source.Twitter -> 2
        else -> 0
    }

    @TypeConverter
    fun intToSource(int: Int) = when (int) {
        1 -> Source.Fanfou
        2 -> Source.Twitter
        else -> Source.Unknown
    }
}