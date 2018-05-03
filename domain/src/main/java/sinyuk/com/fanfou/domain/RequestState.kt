package sinyuk.com.fanfou.domain

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

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


enum class RequestState {
    RUNNING,
    SUCCESS,
    FAILED,
}


@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: RequestState,
        val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(RequestState.SUCCESS)
        val LOADING = NetworkState(RequestState.RUNNING)
        fun error(msg: String?) = NetworkState(RequestState.FAILED, msg)
    }

    override fun equals(other: Any?): Boolean {
        if (other is NetworkState) {
            return other.status == status && other.msg == msg
        }
        return false
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (msg?.hashCode() ?: 0)
        return result
    }
}


@SuppressLint("MissingPermission")
fun isOnline(application: Context): Boolean {
    val connMgr = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

