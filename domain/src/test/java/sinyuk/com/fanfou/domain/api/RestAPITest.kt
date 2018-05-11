package sinyuk.com.fanfou.domain.api

import android.arch.core.executor.testing.InstantTaskExecutorRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import sinyuk.com.fanfou.domain.util.getValue
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Named


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
class RestAPITest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var mockWebServer: MockWebServer

    @field:[Named("test")]
    @Inject
    lateinit var service: RestAPI

    @Before
    @Throws(IOException::class)
    fun createService() {
        mockWebServer = MockWebServer()
        DaggerTestApiComponent.builder()
                .testApiModule(TestApiModule(mockWebServer))
                .apiModule(ApiModule()).build().inject(this)
    }

    @After
    @Throws(IOException::class)
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun getStatuses() {
        enqueueResponse("home.json")
        val statuses = getValue(service.statuses_from_path(path = "mock", count = 5))

        val request = mockWebServer.takeRequest()
        assert(request.path.startsWith("/home/mock.json", true))
        assert(request.method.equals("GET", true))
        assert(5 == statuses?.size)

        val status = statuses?.first()
        assert(status?.uniqueId == null)
        assert(status?.id == "KimkREBZynI")
        val createdAt = SimpleDateFormat(CREATED_AT_FORMAT)
                .parse("Sun Apr 22 17:41:59 +0000 2018")
        assert(status?.createdAt == createdAt)
        assert(status?.favorited == false)


        val player = status?.player
        assert(player?.uniqueId == "~3BRijs9l-z4")
        assert(player?.screenName == "钟永健")
        val birthday = SimpleDateFormat(BIRTHDAY_FORMAT)
                .parse("1982-09-03")
        assert(player?.birthday == birthday)
        val createdAt2 = SimpleDateFormat(CREATED_AT_FORMAT)
                .parse("Sat May 12 14:44:44 +0000 2007")
        assert(player?.createdAt == createdAt2)

        val status2 = statuses?.get(1)
        assert(status2?.photos?.url == "http://fanfou.com/photo/TZpJWglbjqs")
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun verifyCredentials() {
        enqueueResponse("verify_credentials.json")
        val apiResponse = getValue(service.verify_credentials())
        assert(apiResponse.code == 200)

        val request = mockWebServer.takeRequest()
        assert(request.path.startsWith("/account/verify_credentials.json", true))
        assert(request.method.equals("GET", true))

        val player = apiResponse.body
        assert(player?.screenName == "开膛手椰壳")
        assert(player?.uniqueId == "~x9SCdqM0Kks")
        val birthday = SimpleDateFormat(BIRTHDAY_FORMAT)
                .parse("1993-11-29")
        assert(player?.birthday == birthday)
        val createdAt = SimpleDateFormat(CREATED_AT_FORMAT)
                .parse("Wed May 04 15:32:29 +0000 2016")
        assert(player?.createdAt == createdAt)
    }


    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun showUser() {
        enqueueResponse("show_user.json")
        val uniqueId = "~lwumOR8xxOI"
        val apiResponse = getValue(service.show_user(uniqueId))
        assert(apiResponse.code == 200)

        val request = mockWebServer.takeRequest()
        assert(request.requestUrl.queryParameter("id") == uniqueId)
        val player = apiResponse.body
        assert(player?.screenName == "阪本")
        val birthday = SimpleDateFormat(BIRTHDAY_FORMAT)
                .parse("1993-01-13")
        assert(player?.birthday == birthday)
        val createdAt = SimpleDateFormat(CREATED_AT_FORMAT)
                .parse("Tue Apr 03 17:11:06 +0000 2012")
        assert(player?.createdAt == createdAt)
        assert(player?.following == true)
        assert(player?.notifications == true)

        val status = player?.status!!
        assert("_dd5X-tpqvc" == status.id)

    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun fetchLatestStatus() {
        enqueueResponse("fetch_latest_status.json")
        val uniqueId = "~lwumOR8xxOI"
        val status = getValue(service.fetch_latest_status(uniqueId))?.first()

        val request = mockWebServer.takeRequest()
        assert(request.requestUrl.queryParameter("id") == uniqueId)
        assert(request.requestUrl.queryParameter("count") == "1")
        assert(request.requestUrl.queryParameter("format") == "html")
        assert("_dd5X-tpqvc" == status?.id)
        val player = status?.player
        assert(uniqueId == player?.uniqueId)
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)))
    }
}
