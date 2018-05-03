package sinyuk.com.fanfou.domain.api

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sinyuk.com.fanfou.domain.api.adapters.LiveDataCallAdapterFactory
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
@Module(includes = [ApiModule::class])
class TestApiModule constructor(private val mockWebServer: MockWebServer) {
    @Suppress("unused")
    @Provides
    @Singleton
    fun provideEndpoint() = Endpoint("/")

    @Suppress("unused")
    @Provides
    @Singleton
    fun provideRestAPI(endpoint: Endpoint, gson: Gson): RestAPI = Retrofit.Builder()
            .baseUrl(mockWebServer.url(endpoint.baseUrl))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(RestAPI::class.java)
}