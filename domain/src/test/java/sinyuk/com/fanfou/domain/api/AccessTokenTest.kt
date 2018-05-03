package sinyuk.com.fanfou.domain.api

import okio.Okio

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import sinyuk.com.fanfou.domain.util.loadResponseFromAssets
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Created by sinyuk on 2018/4/27.
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
class AccessTokenTest {

    @Test
    fun parseResponse() {
        val responseString = loadResponseFromAssets(this.javaClass, "access_token_succeed.txt")!!
        val accessToken = AccessToken.parseResponse(responseString)!!

        assert(accessToken.token == "1394833-dcb00385a3f550be20880ad428c7917d")
        assert(accessToken.secret == "6c561874817975548f4c352b78f29ded")
    }


}