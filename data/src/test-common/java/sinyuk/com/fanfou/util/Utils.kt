package sinyuk.com.fanfou.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import okio.Okio
import retrofit2.Call
import java.io.IOException
import java.nio.charset.StandardCharsets

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
@Suppress("UNCHECKED_CAST")
@Throws(InterruptedException::class)
fun <T> getValue(liveData: LiveData<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data[0] = o
            latch.countDown()
            liveData.removeObserver(this)
        }
    }
    liveData.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)

    return data[0] as T
}


fun <T> getValue(call: Call<T>): T? {
    val response = call.execute()
    return response.body()
}


@Throws(IOException::class)
fun loadResponseFromAssets(javaClass: Class<*>, fileName: String): String? {
    val inputStream = javaClass.classLoader
            .getResourceAsStream("api-response/$fileName")
    val source = Okio.buffer(Okio.source(inputStream))
    return source.readString(StandardCharsets.UTF_8)
}
