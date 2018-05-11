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

package sinyuk.com.fanfou.domain.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sinyuk.com.fanfou.domain.AppExecutors
import sinyuk.com.fanfou.domain.Promise
import sinyuk.com.fanfou.domain.api.RestAPI
import sinyuk.com.fanfou.domain.data.Player
import sinyuk.com.fanfou.domain.usecase.PlayerUsecase
import java.io.IOException
import java.io.InterruptedIOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/5/8.
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
class PlayerRepo @Inject constructor(private val restAPI: RestAPI,
                                     private val appExecutors: AppExecutors) : PlayerUsecase {
    override fun fetchLatestStatus(uniqueId: String, forcedUpdate: Boolean): LiveData<Promise<Player>> {
        val liveData = MutableLiveData<Promise<Player>>()
        liveData.postValue(Promise.loading(null))
        if (forcedUpdate) {
            appExecutors.networkIO().execute {
                try {
                    val response = restAPI.fetch_latest_status(uniqueId).execute()
                    if (response.isSuccessful) {
                        val status = response.body()?.first()
                        if (status != null) {
                            val player = status.player
                            player?.status = status
                            liveData.postValue(Promise.success(player))
                        } else {
                            liveData.postValue(Promise.success(null))
                        }
                    } else {
                        liveData.postValue(Promise.error(response.message(), null))
                    }
                } catch (e: IOException) {
                    liveData.postValue(Promise.error(e.message, null))
                } catch (e: InterruptedIOException) {
                    liveData.postValue(Promise.error(e.message, null))
                }
            }
        } else {

        }
        return liveData
    }


}