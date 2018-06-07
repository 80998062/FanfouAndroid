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

package sinyuk.com.fanfou.api

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sinyuk.com.common.api.adapters.LiveDataCallAdapterFactory
import sinyuk.com.fanfou.util.getValue
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*


/**
 * Created by sinyuk on 2018/4/23.
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
@RunWith(JUnit4::class)
class FanfouAPITest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var mockWebServer: MockWebServer

    private lateinit var service: FanfouAPI

    @Before
    @Throws(IOException::class)
    fun createService() {
        mockWebServer = MockWebServer()
        val gson = GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Date::class.java, DateDeserializer)
                .create()

        service = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(FanfouAPI::class.java)
    }

    @After
    @Throws(IOException::class)
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun verifyCredentials() {
        enqueueResponse("verify_credentials.json")
        val apiResponse = getValue(service.verifyCredentials())
        assert(apiResponse.code == 200)

        val player = apiResponse.body!!
        assert("Sinyuk" == player.id)
        assert("沈烨坷" == player.name)
        assert("开膛手椰壳" == player.screenName)
        assert(player.notifications == true)
        assert(player.followRequestSent == false)
        assert(player.following == true)
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun showUser() {
        enqueueResponse("users_show.json")
        val apiResponse = getValue(service.showUser(""))
        assert(apiResponse.code == 200)

        val player = apiResponse.body!!
        assert("sakamotosan" == player.id)
        assert("阪本" == player.name)
        assert("阪本" == player.screenName)
        assert(player.notifications == true)
        assert(player.followRequestSent == false)
        assert(player.following == true)
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun friends() {
        enqueueResponse("users_friends.json")
        val apiResponse = getValue(service.friends())
        assert(apiResponse.code == 200)

        val players = apiResponse.body!!
        assert(players.size == 10)
        assert(players.first().id == "aki_sakura")
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/fanfou/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)))
    }
}
