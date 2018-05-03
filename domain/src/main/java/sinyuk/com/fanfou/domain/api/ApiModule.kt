package sinyuk.com.fanfou.domain.api

import com.google.gson.*
import dagger.Module
import dagger.Provides
import sinyuk.com.fanfou.domain.data.Status
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
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
}