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

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.support.annotation.VisibleForTesting
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.*
import okhttp3.CacheControl
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.ByteString
import sinyuk.com.fanfou.BuildConfig
import sinyuk.com.fanfou.BuildConfig.OAUTH_VERSION_VALUE
import sinyuk.com.fanfou.TimberDelegate
import sinyuk.com.fanfou.domain.ACCESS_TOKEN
import sinyuk.com.fanfou.domain.api.AccessTokenTask
import sinyuk.com.fanfou.domain.isOnline
import sinyuk.com.fanfou.domain.utils.UrlEscapeUtils
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_ACCESS_TOKEN
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_CONSUMER_KEY
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_NONCE
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_SIGNATURE
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_SIGNATURE_METHOD
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_SIGNATURE_METHOD_VALUE
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_TIMESTAMP
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_VERSION
import java.io.File
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


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

fun initOkHttpClient(application: Context,
                     tokenSet: MutableSet<String?>?,
                     authenticator: FanfouAuthenticator): OkHttpClient {
    val timeout: Long = 30
    val max = (1024 * 1024 * 10).toLong() // 10 MiB
    val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
        TimberDelegate.tag("FanfouAPI").d(it)
    })

    val builder = OkHttpClient.Builder()
    builder.retryOnConnectionFailure(true)
    builder.connectTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
    // Response Caching
    val cacheFile = File(application.cacheDir, "http")
    val cache = Cache(cacheFile, max)
    builder.cache(cache)
    builder.addInterceptor(LocalCacheInterceptor(application))
    builder.addNetworkInterceptor(RewriteCacheControlInterceptor(application))

    logging.level = if (BuildConfig.DEBUG) {
        builder.addNetworkInterceptor(StethoInterceptor())
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.HEADERS
    }
    builder.addNetworkInterceptor(logging)

    if (tokenSet?.isNotEmpty() == true) {
        val token = tokenSet.elementAt(1)
        val secret = tokenSet.elementAt(0)
        builder.addNetworkInterceptor(Oauth1SigningInterceptor(token, secret))
    }
    builder.authenticator(authenticator)
    return builder.build()
}

/**
 * interceptor that only work with local cache.
 * you should use @{OkHttpClient#addInterceptor()} to add it.
 */
class LocalCacheInterceptor constructor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        return if (isOnline(context.applicationContext)) {
            val maxAge = 7 * 24 * 60 * 3600
            val mayExpired = CacheControl.Builder()
                    .maxAge(maxAge, TimeUnit.SECONDS)
                    .maxStale(0, TimeUnit.SECONDS).build()
            chain.proceed(builder.cacheControl(mayExpired).build())
        } else {
            val onlyCache = CacheControl.Builder().maxAge(Int.MAX_VALUE, TimeUnit.SECONDS).build()
            chain.proceed(builder.cacheControl(onlyCache).build())
        }
    }
}

/**
 * Interceptor that rewrite the response returned by server side.
 * you should use @{OkHttpClient#addNetworkInterceptor()} to add it.
 * <p/>
 *
 * Notice: it's dangerous!
 */
class RewriteCacheControlInterceptor constructor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)
        return if (isOnline(context)) {
            val cacheControl = "public, max-age=" + (3600 * 60)
            originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", cacheControl)
                    .build()
        } else {
            originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + Int.MAX_VALUE)
                    .build()
        }
    }

}

/**
 * The type Oauth 1 signing interceptor.
 */
class Oauth1SigningInterceptor constructor(private var token: String? = null,
                                           private var secret: String? = null) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (token == null || secret == null) {
            chain.proceed(chain.request().newBuilder().removeHeader("Authorization").build())
        } else {
            val credential = generateCredential(token, secret, chain.request())
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            chain.proceed(chain.request().newBuilder().addHeader("Authorization", credential).build())
        }
    }
}

/**
 * Automatically retry unauthenticated requests, when a response is 401 Not Authorized.
 */
class FanfouAuthenticator constructor(private val preferences: SharedPreferences) : Authenticator {
    companion object {
        const val MAX_RETRY_COUNT = 3
        const val TAG = "FanfouAuthenticator"
        const val OAUTH_CONSUMER_KEY = "oauth_consumer_key"
        const val OAUTH_NONCE = "oauth_nonce"
        const val OAUTH_SIGNATURE = "oauth_signature"
        const val OAUTH_SIGNATURE_METHOD = "oauth_signature_method"
        const val OAUTH_SIGNATURE_METHOD_VALUE = "HMAC-SHA1"
        const val OAUTH_TIMESTAMP = "oauth_timestamp"
        const val OAUTH_ACCESS_TOKEN = "oauth_token"
        const val OAUTH_VERSION = "oauth_version"
    }

    override fun authenticate(route: Route?, response: Response?): Request? {
        if (response == null) return null
        if (response.request()?.header("Authorization") != null) {
            TimberDelegate.tag(TAG).d("Authenticating for response: $response")
            TimberDelegate.tag(TAG).d("Challenges: %s", response.challenges())
            return null
        }
        println("authenticate")
        val stringSet = preferences.getStringSet(ACCESS_TOKEN, null)
        val credential = if (stringSet?.isNotEmpty() == true) {
            val token = stringSet.elementAt(1)
            val secret = stringSet.elementAt(0)
            generateCredential(token, secret, response.request())
        } else {
            "Should have already generated credential here !"
        }

        when {
            credential == null -> return null
            responseCount(response) >= MAX_RETRY_COUNT -> return null // If we've failed 3 times, give up.
            credential == response.request().header("Authorization") -> return null
            else -> response.request().newBuilder().header("Authorization", credential).build()
        }

        return null
    }


    private fun responseCount(response: Response?): Int {
        var current = response
        var result = 0
        while (current != null) {
            result++
            current = current.priorResponse()
        }
        return result
    }
}

//1SsCtw5eyFFHigWXMcodkSYmtcZKv9IgSZXFKa7YnfA
fun generateCredential(token: String?,
                       secret: String?,
                       request: Request,
                       @VisibleForTesting mockNonce: String? = null,
                       @VisibleForTesting mockTimestamp: String? = null): String? {
    if (token == null || secret == null) return null
    val random = SecureRandom()
    val bytes = ByteArray(32)
    random.nextBytes(bytes)

    val nonce = mockNonce ?: ByteString.of(*bytes).base64().replace("\\W".toRegex(), "")
    val timestamp = mockTimestamp ?: AccessTokenTask.Clock().millis()

    val parameters = TreeMap<String, String>()

    val consumerKeyValue = UrlEscapeUtils.escape(BuildConfig.CONSUMER_KEY)
    val accessTokenValue = UrlEscapeUtils.escape(token)

    parameters[OAUTH_CONSUMER_KEY] = consumerKeyValue
    parameters[OAUTH_ACCESS_TOKEN] = accessTokenValue
    parameters[OAUTH_NONCE] = nonce
    parameters[OAUTH_TIMESTAMP] = timestamp
    parameters[OAUTH_SIGNATURE_METHOD] = OAUTH_SIGNATURE_METHOD_VALUE
    parameters[OAUTH_VERSION] = BuildConfig.OAUTH_VERSION_VALUE

    val httpUrl = request.url()
    // 如果请求是GET, 则QueryString中所有的参数都参与签名
    // 如果请求是POST并且，Content-Type是application/x-www-form-urlencoded, 则所有的POST参数需要参加签名
    if (request.method() == "GET" || (request.method() == "POST"
                    && request.header("Content-Type") == "application/x-www-form-urlencoded")) {
        val querySize = request.url().querySize()
        for (i in 0 until querySize) {
            parameters[httpUrl.queryParameterName(i)] = httpUrl.queryParameterValue(i)
        }
    }

    // no need for request body
    val base = Buffer()
    val method = request.method()
    base.writeUtf8(method)
    base.writeByte('&'.toInt())
    base.writeUtf8(UrlEscapeUtils.escape(request.url().newBuilder().query(null).build().toString()))
    base.writeByte('&'.toInt())

    var first = true
    for (entry in parameters.entries) {
        if (!first) base.writeUtf8(UrlEscapeUtils.escape("&"))
        first = false
        base.writeUtf8(entry.key)
        base.writeUtf8(UrlEscapeUtils.escape("="))
        base.writeUtf8(entry.value)
    }
    val signingKey = BuildConfig.CONSUMER_SECRET + "&" + secret
    val baseString = String(base.readByteArray(), Charset.forName("UTF-8"))
    val signature = signature(baseString, signingKey)
    return (BuildConfig.OAUTH_TYPE + " " +
            OAUTH_CONSUMER_KEY + "=\"" + consumerKeyValue + "\", " + OAUTH_NONCE + "=\""
            + nonce + "\", " + OAUTH_SIGNATURE + "=\"" + UrlEscapeUtils.escape(signature)
            + "\", " + OAUTH_SIGNATURE_METHOD + "=\"" + OAUTH_SIGNATURE_METHOD_VALUE + "\", "
            + OAUTH_TIMESTAMP + "=\"" + timestamp + "\", " + OAUTH_ACCESS_TOKEN + "=\""
            + accessTokenValue + "\", " + OAUTH_VERSION + "=\"" + OAUTH_VERSION_VALUE + "\"")
}

@TargetApi(Build.VERSION_CODES.O)
@Throws(IllegalStateException::class, InvalidKeyException::class)
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun signature(baseString: String, signKey: String): String {
    var result = ""
    try {
        val keySpec = SecretKeySpec(signKey.toByteArray(charset("UTF-8")), "HmacSHA1")
        val mac: Mac
        try {
            mac = Mac.getInstance("HmacSHA1")
            mac.init(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException(e)
        } catch (e: InvalidKeyException) {
            throw IllegalStateException(e)
        }
        val rawHmac = mac.doFinal(baseString.toByteArray(charset("UTF-8")))
        result = Base64.getEncoder().encodeToString(rawHmac)
    } catch (e: Exception) {
    }
    return result
}