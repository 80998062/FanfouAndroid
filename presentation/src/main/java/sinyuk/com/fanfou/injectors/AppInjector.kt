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

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import dagger.android.support.AndroidSupportInjection
import sinyuk.com.fanfou.App
import sinyuk.com.fanfou.domain.api.ApiModule
import sinyuk.com.fanfou.domain.room.RoomModule

/**
 * Helper class to automatically inject fragments if they implement [Injectable].
 *
 * @author sinyuk
 */
object AppInjector {

    fun init(app: App) {
        DaggerAppComponent.builder()
                .appModule(AppModule(app))
                .apiModule(ApiModule())
                .roomModule(RoomModule(app)).build().inject(app)

        app.registerActivityLifecycleCallbacks(
                object : Application.ActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, b: Bundle?) {
                        handleFragments(activity)
                    }

                    override fun onActivityStarted(activity: Activity) {

                    }

                    override fun onActivityResumed(activity: Activity) {

                    }

                    override fun onActivityPaused(activity: Activity) {

                    }

                    override fun onActivityStopped(activity: Activity) {

                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

                    }

                    override fun onActivityDestroyed(activity: Activity) {

                    }
                })
    }

    private fun handleFragments(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(fm: FragmentManager?, f: Fragment?,
                                                       savedInstanceState: Bundle?) {
                            if (f is Injectable) AndroidSupportInjection.inject(f)
                        }
                    }, true)
        }

    }
}