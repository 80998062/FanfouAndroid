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

package sinyuk.com.common.realm

import android.app.Application
import dagger.Module
import dagger.Provides
import io.realm.BuildConfig
import io.realm.Realm
import io.realm.RealmConfiguration

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
class RealmModule constructor(application: Application) {
    init {
        Realm.init(application)
        Realm.setDefaultConfiguration(configurationDefault())
    }

    @Suppress("unused", "MemberVisibilityCanBePrivate")
    @Provides
    @Default
    fun configurationDefault(): RealmConfiguration = RealmConfiguration.Builder()
            .name("fanfou.realm")
            .schemaVersion(BuildConfig.VERSION_CODE.toLong())
            .deleteRealmIfMigrationNeeded()
//            .migration()
            .build()


    @Suppress("unused")
    @Provides
    @InMemory
    fun configurationInMemory(): RealmConfiguration = RealmConfiguration.Builder()
            .name("fanfou.realm")
            .schemaVersion(BuildConfig.VERSION_CODE.toLong())
            .deleteRealmIfMigrationNeeded()
            .build()
}