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

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import android.support.annotation.VisibleForTesting
import okhttp3.HttpUrl
import okhttp3.Response
import okio.ByteString
import org.xml.sax.SAXException
import sinyuk.com.common.ACCESS_TOKEN
import sinyuk.com.common.Promise
import sinyuk.com.common.realm.model.BuildConfig
import java.io.IOException
import java.io.InterruptedIOException
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * Created by sinyuk on 2018/4/24.
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
class FanfouAccessTokenTask @Inject constructor(
        account: String,
        password: String,
        private val preferences: SharedPreferences?,
        private val execute: (url: HttpUrl) -> Response?) : Runnable {

    @Suppress("MemberVisibilityCanBePrivate")
    val liveData: MutableLiveData<Promise<FanfouAccessToken>> = MutableLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var url: HttpUrl

    init {
        liveData.postValue(Promise.loading(null))
        url = accessTokenUrl(account, password)
    }

    @VisibleForTesting
    override fun run() {
        try {
            val response = execute(url)
            if (response == null) {
                liveData.postValue(Promise.error(INTERNAL_ERROR, null))
            } else {
                parse(response)
            }
        } catch (e: IOException) {
            liveData.postValue(Promise.error(e.message, null))
        } catch (e: InterruptedIOException) {
            liveData.postValue(Promise.error(e.message, null))
        }
    }

    @Throws(InterruptedIOException::class, IOException::class)
    private fun parse(response: Response) {
        if (response.body() == null) throw IOException(SERVER_ERROR)
        val text = response.body()!!.string()
        if (response.code() == 200 && text != null) {
            val accessToken = FanfouAccessToken.parseResponse(text)
            if (accessToken == null) {
                liveData.postValue(Promise.error(INTERNAL_ERROR, null))
            } else {
                preferences?.edit()
                        ?.putStringSet(ACCESS_TOKEN, mutableSetOf(accessToken.token, accessToken.secret))
                        ?.apply()
                liveData.postValue(Promise.success(accessToken))
            }
        } else {
            liveData.postValue(Promise.error(parseFailedResponse(text), null))
        }
    }


    /**
     * 匹配中文
     */
    private val abs: Pattern = Pattern.compile("(\\d+)\u4e2a\u6587\u4ef6")

    @SuppressLint("LogNotTimber")
    private fun parseFailedResponse(xml: String?): String {
        if (xml == null) return SERVER_ERROR
        val text = xml.replace("&", "&amp;")
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        try {
            val stream = text.byteInputStream(Charset.forName("UTF-8"))
            val doc = builder.parse(stream)
            val errors = doc.getElementsByTagName("error")
            if (errors.length > 0) {
                val input = errors.item(0).textContent
                return if (input.contains("xAuth login error ", true)) {
                    input.replace("xAuth login error ", "")
                } else {
                    input
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return INTERNAL_ERROR
    }


    private fun accessTokenUrl(account: String, password: String): HttpUrl {
        val bytes = ByteArray(32)
        getRandom().nextBytes(bytes)
        val nonce = ByteString.of(*bytes).base64().replace("\\W".toRegex(), "")
        val timestamp = Clock().millis()
        return accessTokenUrl(account, password, timestamp, nonce)
    }


    private fun getRandom(): Random {
        return object : Random() {
            override fun nextBytes(bytes: ByteArray) {
                if (bytes.size != 32) throw AssertionError()
                val hex = ByteString.decodeBase64(randomString(8))
                val nonce = hex!!.toByteArray()
                System.arraycopy(nonce, 0, bytes, 0, nonce.size)
            }
        }
    }

    private fun randomString(length: Int): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val buf = StringBuilder()
        for (i in 0 until length) {
            val num = random.nextInt(str.length - 1)
            buf.append(str[num])
        }
        return buf.toString()
    }


    /**
     * Simple clock like class, to allow time mocking.
     */
    class Clock {
        fun millis(): String {
            return (System.currentTimeMillis() / 1000L).toString()
        }
    }

    companion object {
        const val CONSUMER_KEY = "oauth_consumer_key"
        const val NONCE = "nonce"
        const val METHOD_GET = "GET"
        const val SIGNATURE = "signature"
        const val SIGNATURE_METHOD = "signature_method"
        const val SIGNATURE_METHOD_VALUE = "HMAC-SHA1"
        const val TIMESTAMP = "timestamp"
        const val VERSION = "version"
        const val VERSION_VALUE = "1.0"
        const val X_AUTH_USERNAME = "x_auth_username"
        const val X_AUTH_PASSWORD = "x_auth_password"
        const val X_AUTH_MODE = "x_auth_mode"

        const val SERVER_ERROR = "服务器返回了一个异常"
        const val INTERNAL_ERROR = "你竟然在代码里下毒( ˚ཫ˚ )"
        @Suppress("MemberVisibilityCanBePrivate")
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun accessTokenUrl(account: String, password: String, timestamp: String, nonce: String): HttpUrl {
            val parameters = TreeMap<String, String>()
            parameters[CONSUMER_KEY] = BuildConfig.CONSUMER_KEY
            // parameters for XAuth
            parameters[X_AUTH_USERNAME] = account
            parameters[X_AUTH_PASSWORD] = password
            parameters[X_AUTH_MODE] = BuildConfig.X_AUTH_MODE
            parameters[NONCE] = nonce
            parameters[TIMESTAMP] = timestamp
            parameters[SIGNATURE_METHOD] = SIGNATURE_METHOD_VALUE
            parameters[VERSION] = VERSION_VALUE

            var first = true
            val sb = StringBuilder("http://fanfou.com/oauth/access_token")

            for ((key, value) in parameters) {
                if (first) {
                    sb.append("?")
                    first = false
                } else {
                    sb.append("&")
                }
                sb.append(key).append("=").append(value)
            }

            return HttpUrl.parse(sb.toString())!!
        }

    }
}

