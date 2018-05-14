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

package sinyuk.com.fanfou

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.support.v7.app.AppCompatDelegate
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import sinyuk.com.common.AppExecutors
import sinyuk.com.common.TYPE_GLOBAL
import sinyuk.com.fanfou.injectors.AppInjector
import sinyuk.com.fanfou.ui.NIGHT_MODE
import sinyuk.com.fanfou.ui.NIGHT_MODE_AUTO
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by sinyuk on 2018/5/3.
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
class App : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

    @Suppress("MemberVisibilityCanBePrivate")
    @Inject
    lateinit var appExecutors: AppExecutors

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        TimberDelegate.plantTree()

        InstallTask(this).apply {
            appExecutors.networkIO().execute(this)
        }

        configureNightMode()
    }


    @field:[Named(TYPE_GLOBAL) Inject]
    lateinit var preferences: SharedPreferences

    /**
     * Setup night mode
     */
    private fun configureNightMode() {
        if (preferences.getBoolean(NIGHT_MODE_AUTO, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        } else {
            if (preferences.getBoolean(NIGHT_MODE, false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}