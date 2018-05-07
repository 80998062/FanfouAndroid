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

package sinyuk.com.fanfou.domain.repo

import android.arch.lifecycle.LiveData
import android.content.SharedPreferences
import okhttp3.HttpUrl
import okhttp3.Response
import sinyuk.com.fanfou.domain.AppExecutors
import sinyuk.com.fanfou.domain.Promise
import sinyuk.com.fanfou.domain.TYPE_GLOBAL
import sinyuk.com.fanfou.domain.api.AccessToken
import sinyuk.com.fanfou.domain.api.AccessTokenTask
import sinyuk.com.fanfou.domain.api.ApiResponse
import sinyuk.com.fanfou.domain.api.RestAPI
import sinyuk.com.fanfou.domain.data.Player
import sinyuk.com.fanfou.domain.usecase.SignUsecase
import javax.inject.Inject
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
@Singleton
class UserRepo @Inject constructor(private val restAPI: RestAPI,
                                   private val appExecutors: AppExecutors,
                                   @Named(TYPE_GLOBAL) private val preferences: SharedPreferences) : SignUsecase {
    override fun vertify(): LiveData<ApiResponse<Player>> {
        return restAPI.verify_credentials()
    }

    override fun signIn(account: String,
                        password: String,
                        execute: (url: HttpUrl) -> Response?): LiveData<Promise<AccessToken>> {
        val task = AccessTokenTask(account, password, preferences, execute)
        return task.apply { appExecutors.networkIO().execute(this) }.liveData
    }
}