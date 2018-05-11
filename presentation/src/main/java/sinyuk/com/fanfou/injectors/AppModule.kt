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

package sinyuk.com.fanfou.injectors

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sinyuk.com.fanfou.App
import sinyuk.com.fanfou.domain.*
import sinyuk.com.fanfou.domain.api.ApiModule
import sinyuk.com.fanfou.domain.api.Endpoint
import sinyuk.com.fanfou.domain.api.RestAPI
import sinyuk.com.fanfou.domain.api.adapters.LiveDataCallAdapterFactory
import sinyuk.com.fanfou.domain.room.RoomModule
import sinyuk.com.fanfou.rest.FanfouAuthenticator
import sinyuk.com.fanfou.rest.initOkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/5/4.
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
@Module(includes = [(ViewModelModule::class), (ApiModule::class), (RoomModule::class)])
class AppModule constructor(private val app: App) {
    @Suppress("unused")
    @Provides
    @Singleton
    fun provideApp(): App = app

    @Suppress("unused")
    @Provides
    @Singleton
    fun provideApplication(): Application = app

    @Suppress("unused")
    @Provides
    @Singleton
    @Named(TYPE_GLOBAL)
    fun providePreferences() = app.getSharedPreferences(TYPE_GLOBAL, Context.MODE_PRIVATE)!!


    @Suppress("unused")
    @Provides
    @Singleton
    fun provideFanfouAuthenticator(@Named(TYPE_GLOBAL) preferences: SharedPreferences) =
            FanfouAuthenticator(preferences)

    @Suppress("unused")
    @Provides
    @Singleton
    @Named(HTTP_FORCED_NETWORK)
    fun provideOkHttp(a: App, @Named(TYPE_GLOBAL) sp: SharedPreferences, fa: FanfouAuthenticator) =
            initOkHttpClient(a, sp.getStringSet(ACCESS_TOKEN, null), fa)

    @Suppress("unused")
    @Provides
    @Singleton
    @Named(HTTP_CACHED)
    fun provideOkHttpCached(a: App, @Named(TYPE_GLOBAL) sp: SharedPreferences, fa: FanfouAuthenticator) =
            initOkHttpClient(a, sp.getStringSet(ACCESS_TOKEN, null), fa, true)

    @Suppress("unused")
    @Provides
    @Singleton
    @Named(HTTP_CACHED)
    fun provideRestAPICached(@Named(HTTP_CACHED) okHttpClient: OkHttpClient, gson: Gson, endpoint: Endpoint) =
            Retrofit.Builder()
                    .baseUrl(endpoint.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .client(okHttpClient)
                    .build()
                    .create(RestAPI::class.java)!!


    @Suppress("unused")
    @Provides
    @Singleton
    @Named(HTTP_FORCED_NETWORK)
    fun provideRestAPI(@Named(HTTP_FORCED_NETWORK) okHttpClient: OkHttpClient, gson: Gson, endpoint: Endpoint) =
            Retrofit.Builder()
                    .baseUrl(endpoint.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .client(okHttpClient)
                    .build()
                    .create(RestAPI::class.java)!!


    @Suppress("unused")
    @Provides
    @Singleton
    fun provideExecutors() = AppExecutors()
}