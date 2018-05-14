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
import com.twitter.sdk.android.core.TwitterCore
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sinyuk.com.common.*
import sinyuk.com.common.api.ApiModule
import sinyuk.com.common.api.Endpoint
import sinyuk.com.common.api.adapters.LiveDataCallAdapterFactory
import sinyuk.com.common.room.RoomModule
import sinyuk.com.fanfou.App
import sinyuk.com.fanfou.domain.api.FanfouAPI
import sinyuk.com.fanfou.rest.FanfouAuthenticator
import sinyuk.com.fanfou.rest.initFanfouClient
import sinyuk.com.fanfou.rest.initTwitterClient
import sinyuk.com.twitter.domain.api.TwitterAPI
import sinyuk.com.twitter.domain.api.TwitterClient
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
    @Fanfou
    fun provideFanfou(a: App, @Named(TYPE_GLOBAL) sp: SharedPreferences, fa: FanfouAuthenticator) =
            initFanfouClient(a, sp.getStringSet(ACCESS_TOKEN, null), fa)

    @Suppress("unused")
    @Provides
    @Singleton
    @Fanfou(cached = true)
    fun provideFanfouCached(a: App, @Named(TYPE_GLOBAL) sp: SharedPreferences, fa: FanfouAuthenticator) =
            initFanfouClient(a, sp.getStringSet(ACCESS_TOKEN, null), fa, true)

    @Suppress("unused")
    @Provides
    @Singleton
    @Twitter
    fun provideTwitter(a: App) = initTwitterClient(a, true)

    @Suppress("unused")
    @Provides
    @Singleton
    @Twitter(cached = true)
    fun provideTwitterCached(a: App) = initTwitterClient(a, true)

    @Suppress("unused")
    @Provides
    @Singleton
    @Fanfou(cached = true)
    fun provideFanfouAPICached(@Fanfou(cached = true) oc: OkHttpClient, g: Gson, e: Endpoint) =
            Retrofit.Builder()
                    .baseUrl(e.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(g))
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .client(oc)
                    .build()
                    .create(FanfouAPI::class.java)!!


    @Suppress("unused")
    @Provides
    @Singleton
    @Fanfou
    fun provideFanfouAPI(@Fanfou oc: OkHttpClient, g: Gson, e: Endpoint) =
            Retrofit.Builder()
                    .baseUrl(e.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(g))
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .client(oc)
                    .build()
                    .create(FanfouAPI::class.java)!!

    @Suppress("unused")
    @Provides
    @Singleton
    @Twitter
    fun provideTwitterAPI(@Twitter oc: OkHttpClient): TwitterAPI {
        val session = TwitterCore.getInstance().sessionManager.activeSession
        val client: TwitterClient = if (session != null) {
            TwitterClient(oc, session)
        } else {
            TwitterClient(oc)
        }

        if (session != null) {
            TwitterCore.getInstance().addApiClient(session, client)
        } else {
            TwitterCore.getInstance().addGuestApiClient(client)
        }
        return client.getService()
    }

    @Suppress("unused")
    @Provides
    @Singleton
    @Twitter(cached = true)
    fun provideTwitterAPICached(@Twitter(cached = true) oc: OkHttpClient): TwitterAPI {
        val session = TwitterCore.getInstance().sessionManager.activeSession
        val client: TwitterClient = if (session != null) {
            TwitterClient(oc, session)
        } else {
            TwitterClient(oc)
        }

        if (session != null) {
            TwitterCore.getInstance().addApiClient(session, client)
        } else {
            TwitterCore.getInstance().addGuestApiClient(client)
        }
        return client.getService()
    }

    @Suppress("unused")
    @Provides
    @Singleton
    fun provideExecutors() = AppExecutors()
}