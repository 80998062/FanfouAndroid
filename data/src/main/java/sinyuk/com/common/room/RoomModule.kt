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

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import sinyuk.com.common.ROOM_IN_DISK
import sinyuk.com.common.ROOM_IN_MEMORY
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/4/23.
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
@Module
class RoomModule constructor(private val application: Application) {
    @Suppress("unused")
    @Provides
    @Singleton
    @Named(ROOM_IN_DISK)
    fun provideDiskRoom(): LocalDatabase = Room.databaseBuilder(application, LocalDatabase::class.java, "twifold.db")
            .fallbackToDestructiveMigration()
            .build()


    @Suppress("unused")
    @Provides
    @Singleton
    @Named(ROOM_IN_MEMORY)
    fun provideMemoryRoom(): LocalDatabase = Room.inMemoryDatabaseBuilder(application, LocalDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
}