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

import dagger.Module
import dagger.android.ContributesAndroidInjector
import sinyuk.com.fanfou.ui.home.HomeActivity
import sinyuk.com.fanfou.ui.sign.SignInActivity
import sinyuk.com.fanfou.ui.status.TimelineActivity
import sinyuk.com.fanfou.ui.status.TimelineTestActivity

@Module
abstract class ActivityBuildersModule {

    @Suppress("unused")
    @ActivityScope
    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun homeActivity(): HomeActivity

    @Suppress("unused")
    @ActivityScope
    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun signInActivity(): SignInActivity


    @Suppress("unused")
    @ActivityScope
    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun timelineActivity(): TimelineActivity

    @Suppress("unused")
    @ActivityScope
    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun timelineTestActivity(): TimelineTestActivity

}