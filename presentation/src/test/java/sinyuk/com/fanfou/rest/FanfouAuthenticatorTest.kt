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
        const val nonce = "seqCfH4TmcB8MAR1kDUWaBmmDnrSwjR8AAAAAAAAAAA"
        const val timestamp = "1525403431"

        //    OAuth oauth_consumer_key="ceab0dcd7b9fb9fa2ef5785bcd320e70",oauth_token="1394833-dcb00385a3f550be20880ad428c7917d",oauth_signature_method="HMAC-SHA1",oauth_timestamp="1525403431",oauth_nonce="seqCfH4TmcB8MAR1kDUWaBmmDnrSwjR8AAAAAAAAAAA",oauth_version="1.0",oauth_signature="g6p7veDklDVH2yOWOt%2FrRATLWlo%3D"
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
                .url("http://api.fanfou.com/statuses/home_timeline.json")
                .build()

        val authorization = generateCredential(
                token = accessToken,
                secret = tokenSecret,
                request = mockRequest,
                mockNonce = nonce,
                mockTimestamp = timestamp)!!

        assert(authorization.startsWith("OAuth", false))
        val index = authorization.indexOf("oauth_signature", 0, false)
        assert(index > 0)
        val sub = authorization.substring(index)
        assert(sub.startsWith("oauth_signature=\"g6p7veDklDVH2yOWOt%2FrRATLWlo%3D\"", false))
    }


    @Throws(IOException::class)
    @Test
    fun addOauth1SigningInterceptor() {
        val interceptor = Oauth1SigningInterceptor(accessToken, tokenSecret)
        val okHttpClient = OkHttpClient.Builder().addNetworkInterceptor(interceptor).build()

        mockWebServer.enqueue(MockResponse())
        okHttpClient.newCall(Request.Builder().url(mockWebServer.url("/")).build()).execute()

        val request = mockWebServer.takeRequest()
        assert(request.getHeader("Authorization") != null)
    }
}