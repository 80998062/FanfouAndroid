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

import io.realm.RealmConfiguration
import sinyuk.com.common.Fanfou
import sinyuk.com.common.api.RateLimiter
import sinyuk.com.common.realm.Default
import sinyuk.com.common.realm.InMemory
import sinyuk.com.common.repo.PlayerDataStore
import sinyuk.com.fanfou.api.FanfouAPI
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/6/6.
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
@Fanfou
class PlayerRepo constructor(
        private val api: FanfouAPI,
        @Default private val defaultRealm: RealmConfiguration,
        @InMemory private val memoryRealm: RealmConfiguration) : PlayerDataStore {
    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)


}