package sinyuk.com.fanfou.rest

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.*
import okhttp3.CacheControl
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.ByteString
import sinyuk.com.fanfou.BuildConfig
import sinyuk.com.fanfou.BuildConfig.OAUTH_VERSION_VALUE
import sinyuk.com.fanfou.domain.api.AccessTokenTask
import sinyuk.com.fanfou.domain.isOnline
import sinyuk.com.fanfou.prefs.ACCESS_TOKEN
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_ACCESS_TOKEN
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_CONSUMER_KEY
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_NONCE
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_SIGNATURE
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_SIGNATURE_METHOD
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_SIGNATURE_METHOD_VALUE
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_TIMESTAMP
import sinyuk.com.fanfou.rest.FanfouAuthenticator.Companion.OAUTH_VERSION
import java.io.File
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

fun initOkHttpClient(application: Context, preferences: SharedPreferences): OkHttpClient {
    val timeout: Long = 30
    val max = (1024 * 1024 * 10).toLong() // 10 MiB
    val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
        Log.d("FanfouAPI", it)
    })

    val builder = OkHttpClient.Builder()
            .authenticator(FanfouAuthenticator(preferences))
    if (BuildConfig.DEBUG) {
        builder.addNetworkInterceptor(StethoInterceptor())
    } else {
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        builder.addInterceptor(logging)
    }

    builder.retryOnConnectionFailure(false)
    builder.connectTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
    // Response Caching
    val cacheFile = File(application.cacheDir, "http")
    val cache = Cache(cacheFile, max)
    builder.cache(cache)
    val rewriteCacheControlInterceptor = RewriteCacheControlInterceptor(application)
    builder.addInterceptor(rewriteCacheControlInterceptor)
    builder.addNetworkInterceptor(rewriteCacheControlInterceptor)
    return builder.build()
}

/**
 *
 * Dangerous interceptor that rewrites the server's cache-control header.
 */
class RewriteCacheControlInterceptor constructor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!isOnline(context.applicationContext)) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
        }
        val originalResponse = chain.proceed(request)
        return if (isOnline(context)) {
            val cacheControl = request.cacheControl().toString()
            originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build()
        } else {
            originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=3600")
                    .removeHeader("Pragma")
                    .build()
        }
    }

}

/**
 * The type Oauth 1 signing interceptor.
 */
class Oauth1SigningInterceptor constructor(private val token: String?, private val secret: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = generateCredential(token, secret, chain.request())
        return if (credential == null) {
            chain.proceed(chain.request().newBuilder().removeHeader("Authorization").build())
        } else {
            chain.proceed(chain.request().newBuilder().addHeader("Authorization", credential).build())
        }
    }
}

/**
 * Automatically retry unauthenticated requests, when a response is 401 Not Authorized.
 * @param preferences SharedPreferences
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
            if (BuildConfig.DEBUG) {
                println("$TAG Authenticating for response: $response")
                println("$TAG Challenges: " + response.challenges())
            }
            return null
        }
        val stringSet = preferences.getStringSet(ACCESS_TOKEN, null)
        if (stringSet?.isEmpty() != false || stringSet.size != 2) {
            return null
        } else {
            val token = stringSet.elementAt(0)
            val secret = stringSet.elementAt(1)
            val credential = generateCredential(token, secret, response.request())
            when {
                credential == null -> return null
                responseCount(response) >= MAX_RETRY_COUNT -> return null // If we've failed 3 times, give up.
                credential == response.request().header("Authorization") -> return null
                else -> response.request().newBuilder().header("Authorization", credential).build()
            }
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

fun generateCredential(token: String?, secret: String?, request: Request): String? {
    if (token == null || secret == null) return null
    val random = SecureRandom()
    val bytes = ByteArray(32)
    val clock = AccessTokenTask.Clock()
    random.nextBytes(bytes)
    val nonce = ByteString.of(*bytes).base64().replace("\\W".toRegex(), "")
    val timestamp = clock.millis()

    val parameters = TreeMap<String, String>()

    val consumerKeyValue = UrlEscapeUtils.escape("")
    val accessTokenValue = UrlEscapeUtils.escape(token)

    parameters[OAUTH_CONSUMER_KEY] = consumerKeyValue
    parameters[OAUTH_ACCESS_TOKEN] = accessTokenValue
    parameters[OAUTH_NONCE] = nonce
    parameters[OAUTH_TIMESTAMP] = timestamp
    parameters[OAUTH_SIGNATURE_METHOD] = OAUTH_SIGNATURE_METHOD_VALUE
    parameters[OAUTH_VERSION] = BuildConfig.OAUTH_VERSION_VALUE

    val httpUrl = request.url()
    if (request.method() == "GET" || (request.method() == "POST")) {
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
        base.writeUtf8(UrlEscapeUtils.escape(entry.key))
        base.writeUtf8(UrlEscapeUtils.escape("="))
        // TODO: when entry.key = 'q' ?
        base.writeUtf8(UrlEscapeUtils.escape(entry.value))
    }

    val signingKey = UrlEscapeUtils.escape(BuildConfig.CONSUMER_SECRET) +
            "&" + UrlEscapeUtils.escape(secret)

    val keySpec = SecretKeySpec(signingKey.toByteArray(), "HmacSHA1")
    val mac: Mac
    try {
        mac = Mac.getInstance("HmacSHA1")
        mac.init(keySpec)
    } catch (e: NoSuchAlgorithmException) {
        throw IllegalStateException(e)
    } catch (e: InvalidKeyException) {
        throw IllegalStateException(e)
    }

    val result = mac.doFinal(base.readByteArray())
    val signature = ByteString.of(*result).base64()

    return (BuildConfig.OAUTH_TYPE + " " +
            OAUTH_CONSUMER_KEY + "=\"" + consumerKeyValue + "\", " + OAUTH_NONCE + "=\""
            + nonce + "\", " + OAUTH_SIGNATURE + "=\"" + UrlEscapeUtils.escape(signature)
            + "\", " + OAUTH_SIGNATURE_METHOD + "=\"" + OAUTH_SIGNATURE_METHOD_VALUE + "\", "
            + OAUTH_TIMESTAMP + "=\"" + timestamp + "\", " + OAUTH_ACCESS_TOKEN + "=\""
            + accessTokenValue + "\", " + OAUTH_VERSION + "=\"" + OAUTH_VERSION_VALUE + "\"")
}

