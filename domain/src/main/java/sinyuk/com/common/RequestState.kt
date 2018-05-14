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

package sinyuk.com.common

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

