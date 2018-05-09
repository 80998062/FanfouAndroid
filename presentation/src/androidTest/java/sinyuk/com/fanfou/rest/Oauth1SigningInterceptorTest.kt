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

package sinyuk.com.fanfou.rest

import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import sinyuk.com.fanfou.domain.ACCESS_TOKEN
import sinyuk.com.fanfou.domain.TYPE_ANDROID_TEST
import sinyuk.com.fanfou.injectors.AndroidTestAppModule
import sinyuk.com.fanfou.injectors.DaggerAndroidTestAppComponent
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by sinyuk on 2018/5/9.
 * ┌──────────────────────────────────────────────────────────────────┐
 * │                                                                  │
 * │        _______. __  .__   __. ____    ____  __    __   __  ___   │
 * │       /       ||  | |  \ |  | \   \  /   / |  |  |  | |  |/  /   │
 * │      |   (----`|  | |   \|  |  \   \/   /  |  |  |  | |  '  /    │
 * │       \   \    |  | |  . `  |   \_    _/   |  |  |  | |    <     │
 * │   .----)   |   |  | |  |\   |     |  |     |  `--'  | |  .  \    │
 * │   |_______/    |__| |__| \__|     |__|      \______/  |__|\__\   │
 * │                                                                  │
 * └──────────────────────────────────────────────────────────────────┘
 */
@RunWith(AndroidJUnit4::class)
class Oauth1SigningInterceptorTest {

    @field:[Named(TYPE_ANDROID_TEST)]
    @Inject
    lateinit var preferences: SharedPreferences

    @field:[Named(TYPE_ANDROID_TEST)]
    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        DaggerAndroidTestAppComponent.builder()
                .androidTestAppModule(AndroidTestAppModule(context))
                .build().inject(this)
    }

    @After
    fun tearDown() {
    }

    private val verifyUrl = "http://api.fanfou.com/account/verify_credentials.json"
    private val mockTokenSet = mutableSetOf("1394833-dcb00385a3f550be20880ad428c7917d",
            "6c561874817975548f4c352b78f29ded")

    @Test
    fun notIntercept() {
        assert(preferences.getStringSet(ACCESS_TOKEN, mutableSetOf()).isEmpty())
        val response = okHttpClient.newCall(Request.Builder().url(verifyUrl).build()).execute()
        assert(response.code() == 401)
    }

    @Test
    fun intercept() {
        preferences.edit().putStringSet(ACCESS_TOKEN, mockTokenSet).apply()
        assert(preferences.getStringSet(ACCESS_TOKEN, mutableSetOf()).isNotEmpty())

        val request = Request.Builder().url(verifyUrl).build()
        val response = okHttpClient.newCall(request).execute()

//        assert(response.request().header("Authorization").isNotEmpty())
        println("Authorization: " + response.request().header("Authorization"))
        assert(response.code() == 200)
    }
}