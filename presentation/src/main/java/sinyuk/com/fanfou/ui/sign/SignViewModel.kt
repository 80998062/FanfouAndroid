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

package sinyuk.com.fanfou.ui.sign

import android.arch.lifecycle.ViewModel
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import sinyuk.com.common.Fanfou
import sinyuk.com.common.realm.model.Player
import sinyuk.com.fanfou.repo.AccountRepo
import javax.inject.Inject

/**
 * Created by sinyuk on 2018/5/7.
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
class SignViewModel @Inject constructor(
        private val accountRepo: AccountRepo,
        @Fanfou private val okHttpClient: OkHttpClient) : ViewModel() {

    fun signIn(account: String, password: String) =
            accountRepo.signIn(account, password, { executeRequest(it) })

    fun verifyCredentials() = accountRepo.verifyCredentials()

    fun updateProfile(player: Player, account: String) {
        accountRepo.updateProfile(player, account)
    }

    private fun executeRequest(url: HttpUrl): Response {
        return okHttpClient.newCall(Request.Builder().url(url).build()).execute()
    }
}