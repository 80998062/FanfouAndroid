package sinyuk.com.fanfou.domain.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import java.util.*
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
class ApiModule {
    @Suppress("unused")
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                // Blank fields are included as null instead of being omitted.
                .serializeNulls()
                .registerTypeAdapter(Date::class.java, DateDeserializer)
//                .registerTypeAdapter(Status::class.java, StatusDeserializer)
                .create()
    }


    @Suppress("unused")
    @Provides
    @Singleton
    fun provideEndpoint() = Endpoint("http://api.fanfou.com/")

}