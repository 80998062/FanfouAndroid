package sinyuk.com.fanfou.domain.room

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import sinyuk.com.fanfou.domain.ROOM_IN_DISK
import sinyuk.com.fanfou.domain.ROOM_IN_MEMORY
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
    fun provideDiskRoom(): LocalDatabase = Room.databaseBuilder(application, LocalDatabase::class.java, "fanfou.db")
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