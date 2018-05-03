package sinyuk.com.fanfou.domain.api

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
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
class ProductionApiModule constructor(private val okHttpClient: OkHttpClient) {

    @Suppress("unused")
    @Provides
    @Singleton
    fun provideEndpoint() = Endpoint("http://api.fanfou.com/")


    @Suppress("unused")
    @Provides
    @Singleton
    fun provideRestAPI(endpoint: Endpoint, gson: Gson): RestAPI = Retrofit.Builder()
            .baseUrl(endpoint.baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(RestAPI::class.java)

}