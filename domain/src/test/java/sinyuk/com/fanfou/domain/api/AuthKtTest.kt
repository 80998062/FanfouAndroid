package sinyuk.com.fanfou.domain.api

import android.arch.core.executor.testing.InstantTaskExecutorRule
import okhttp3.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import sinyuk.com.fanfou.domain.States
import sinyuk.com.fanfou.domain.util.getValue
import sinyuk.com.fanfou.domain.util.loadResponseFromAssets

import java.io.IOException

/**
 * Created by sinyuk on 2018/4/24.
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
class AuthKtTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val supposed = HttpUrl.parse("http://fanfou.com/oauth/access_token?oauth_consumer_key=ceab0dcd7b9fb9fa2ef5785bcd320e70&signature_method=HMAC-SHA1&timestamp=1489591502&nonce=6aeJiN&version=1.0&x_auth_password=rabbit7run&x_auth_username=80998062@qq.com&x_auth_mode=client_auth")


    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }


    @Test
    @Throws(IOException::class)
    @Suppress("UNNECESSARY_SAFE_CALL")
    fun accessTokenUrl() {
        val mock = AccessTokenTask.accessTokenUrl(
                "80998062@qq.com",
                "rabbit7run",
                "1489591502",
                "6aeJiN")
        assert(supposed?.host() == mock?.host())
        assert(supposed?.querySize() == supposed?.querySize())
        assert(supposed?.queryParameter("oauth_consumer_key") ==
                mock?.queryParameter("oauth_consumer_key"))

        assert(supposed?.queryParameter("oauth_signature_method") ==
                mock?.queryParameter("oauth_signature_method"))

        assert(supposed?.queryParameter("oauth_timestamp") ==
                mock?.queryParameter("oauth_timestamp"))

        assert(supposed?.queryParameter("oauth_version") ==
                mock?.queryParameter("oauth_version"))

        assert(supposed?.queryParameter("oauth_nonce") ==
                mock?.queryParameter("oauth_nonce"))

        assert(supposed?.queryParameter("x_auth_password") ==
                mock?.queryParameter("x_auth_password"))

        assert(supposed?.queryParameter("x_auth_username") ==
                mock?.queryParameter("x_auth_username"))

        assert(supposed?.queryParameter("x_auth_mode") ==
                mock?.queryParameter("x_auth_mode"))
    }

    @Test
    @Throws(IOException::class)
    fun run() {
        val mockTask = AccessTokenTask("", "", { mockSucceed(it) })
        mockTask.run()
        val promise = getValue(mockTask.liveData)
        assert(promise.states == States.SUCCESS)
        assert(promise.data!!.token == "1394833-dcb00385a3f550be20880ad428c7917d")
    }

    @Test
    @Throws(IOException::class)
    fun run1() {
        val mockTask = AccessTokenTask("", "", { mockEmptyBody(it) })
        mockTask.run()
        val promise = getValue(mockTask.liveData)
        assert(promise.states == States.ERROR)
        assert(promise.data == null)
        assert(promise.message == AccessTokenTask.SERVER_ERROR)
    }

    @Test
    @Throws(IOException::class)
    fun run2() {
        val mockTask = AccessTokenTask("", "", { mockFailed(it) })
        mockTask.run()
        val promise = getValue(mockTask.liveData)
        assert(promise.states == States.ERROR)
        assert(promise.data == null)
        assert(promise.message == "xAuth login error 登录失败，Email或密码错误 username=80998062@qq.com")
    }

    @Test
    @Throws(IOException::class)
    fun run3() {
        val mockTask = AccessTokenTask("", "", { mockException(it) })
        mockTask.run()
        val promise = getValue(mockTask.liveData)
        assert(promise.states == States.ERROR)
        assert(promise.data == null)
        assert(promise.message == "timeout")
    }

    private val mediaType = MediaType.parse("application/json")

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun mockSucceed(url: HttpUrl) = Response.Builder()
            .request(Request.Builder().url(url).build())
            .body(ResponseBody.create(mediaType,
                    loadResponseFromAssets(this.javaClass, "access_token_succeed.txt")))
            .protocol(Protocol.HTTP_1_1)
            .message("Succeed")
            .code(200)
            .build()


    private fun mockEmptyBody(url: HttpUrl) = Response.Builder()
            .request(Request.Builder().url(url).build())
            .body(null)
            .protocol(Protocol.HTTP_1_1)
            .message("Failed")
            .code(500)
            .build()


    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun mockFailed(url: HttpUrl) = Response.Builder()
            .request(Request.Builder().url(url).build())
            .body(ResponseBody.create(mediaType,
                    loadResponseFromAssets(this.javaClass, "access_token_failed.xml")))
            .protocol(Protocol.HTTP_1_1)
            .message("Failed")
            .code(401)
            .build()

    @Throws(IOException::class)
    private fun mockException(@Suppress("UNUSED_PARAMETER") url: HttpUrl): Response =
            throw IOException("timeout")

}