package sinyuk.com.fanfou.domain.room

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
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
    @Named("disk")
    fun provideDiskRoom(): LocalDatabase = Room.databaseBuilder(application, LocalDatabase::class.java, "fanfou.db")
            .fallbackToDestructiveMigration()
            .build()


    @Suppress("unused")
    @Provides
    @Singleton
    @Named("memory")
    fun provideMemoryRoom(): LocalDatabase = Room.inMemoryDatabaseBuilder(application, LocalDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()

    @Suppress("unused")
    @Provides
    @Singleton
    @Named("memory")
    fun memoryStatusDao(localDatabase: LocalDatabase) = localDatabase.statusDao()

    @Suppress("unused")
    @Provides
    @Singleton
    @Named("disk")
    fun diskStatusDao(localDatabase: LocalDatabase) = localDatabase.statusDao()
}