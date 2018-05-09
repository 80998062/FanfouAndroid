/*
 * Copyright 2018 Sinyuk
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package sinyuk.com.fanfou.rest

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import sinyuk.com.fanfou.BuildConfig
import java.io.IOException

/**
 * Created by sinyuk on 2018/5/3.
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
class FanfouAuthenticatorTest {

    companion object {
        const val accessToken = "1394833-dcb00385a3f550be20880ad428c7917d"
        const val tokenSecret = "6c561874817975548f4c352b78f29ded"
    }

    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(IOException::class)
    fun start() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Throws(IOException::class)
    @Test
    fun testGenerateCredential() {
        val mockRequest = Request.Builder()
                .method("GET", null)
                .url("http://api.fanfou.com/statuses/user_timeline.json")
                .build()

        val authorization = generateCredential(
                token = accessToken,
                secret = tokenSecret,
                request = mockRequest,
                mockNonce = "1SsCtw5eyFFHigWXMcodkSYmtcZKv9IgSZXFKa7YnfA",
                mockTimestamp = "1525847764")!!

        assert(authorization.startsWith("OAuth", false))
        val index = authorization.indexOf("oauth_signature", 0, false)
        assert(index > 0)
        val sub = authorization.substring(index)
        println(sub)
        assert(sub.startsWith("oauth_signature=\"x08ZzNd6zUOio5M7cPENoUfg%2FZo%3D\"", false))
    }


    @Throws(IOException::class)
    @Test
    fun testGenerateCredential2() {
        val mockRequest = Request.Builder()
                .method("GET", null)
                .url("http://api.fanfou.com/statuses/user_timeline.json?id=~lwumOR8xxOI&count=1")
                .build()

        val authorization = generateCredential(
                token = accessToken,
                secret = tokenSecret,
                request = mockRequest,
                mockNonce = "1SsCtw5eyFFHigWXMcodkSYmtcZKv9IgSZXFKa7YnfA",
                mockTimestamp = "1525847764")!!

        assert(authorization.startsWith("OAuth", false))
        val index = authorization.indexOf("oauth_signature", 0, false)
        assert(index > 0)
        val sub = authorization.substring(index)
        println(sub)
        assert(sub.startsWith("oauth_signature=\"JXM5eJR5xEixHJIQrmxSht3qC6E%3D\"", false))
    }


    @Throws(IOException::class)
    @Test
    fun generateHmacSha1Key() {
        // timestamp: 1525846230

        val baseString = "GET&http%3A%2F%2Fapi.fanfou.com%2Fstatuses%2Fuser_timeline.json&count%3D1%26id%3D~lwumOR8xxOI%26oauth_consumer_key%3Dceab0dcd7b9fb9fa2ef5785bcd320e70%26oauth_nonce%3D1SsCtw5eyFFHigWXMcodkSYmtcZKv9IgSZXFKa7YnfA%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1525846815%26oauth_token%3D1394833-dcb00385a3f550be20880ad428c7917d%26oauth_version%3D1.0"
        val key = BuildConfig.CONSUMER_SECRET + "&" + tokenSecret
        val output = "sO3i8EveDf7Jr8qJSavmxnKWoOw="
        assert(signature(baseString, key) == output)
    }

    @Throws(IOException::class)
    @Test
    fun addOauth1SigningInterceptor() {
        val interceptor = Oauth1SigningInterceptor(accessToken, tokenSecret)
        val okHttpClient = OkHttpClient.Builder().addNetworkInterceptor(interceptor).build()

        mockWebServer.enqueue(MockResponse())
        okHttpClient.newCall(Request.Builder().url(mockWebServer.url("/")).build()).execute()

        val request = mockWebServer.takeRequest()
        val authorization = request.getHeader("Authorization")

        assert(authorization.startsWith("OAuth", false))
    }
}