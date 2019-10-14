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

package sinyuk.com.fanfou.repo

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.HttpUrl
import okhttp3.Response
import sinyuk.com.common.AppExecutors
import sinyuk.com.common.Fanfou
import sinyuk.com.common.Promise
import sinyuk.com.common.api.RateLimiter
import sinyuk.com.common.realm.Default
import sinyuk.com.common.realm.model.Account
import sinyuk.com.common.realm.model.Player
import sinyuk.com.common.realm.model.SOURCE_FANFOU
import sinyuk.com.fanfou.api.FanfouAPI
import sinyuk.com.fanfou.api.FanfouAccessToken
import sinyuk.com.fanfou.api.FanfouAccessTokenTask
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/6/7.
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
@Fanfou
@Singleton
class AccountRepo @Inject constructor(
        private val api: FanfouAPI,
        private val appExecutors: AppExecutors,
        @Default private val defaultRealm: RealmConfiguration,
        @Fanfou private val rateLimiter: RateLimiter<String>) {

    /**
     * Get access token for a Fanfou user
     *
     * @param account account
     * @param password password
     * @param execute method that execute by the given OkHttpClient
     */
    fun signIn(account: String,
               password: String,
               execute: (url: HttpUrl) -> Response?): LiveData<Promise<FanfouAccessToken>> {
        val task = FanfouAccessTokenTask(account, password, defaultRealm, execute)
        return task.apply { appExecutors.networkIO().execute(this) }.liveData
    }

    /**
     * verify credentials of current logged Fanfou user
     */
    fun verifyCredentials() = api.verifyCredentials()

    /**
     * Update account's profile
     */
    fun updateProfile(player: Player, account: String? = null) {
        Realm.getInstance(defaultRealm).executeTransaction {
            player.source = SOURCE_FANFOU
            it.insertOrUpdate(player)
            if (account != null) {
                val result = it.where(Account::class.java)
                        .equalTo("account", account).findFirst()
                result?.updatedAt = Date()
                result?.avatar = player.profileImageUrl
            }
        }
    }
}