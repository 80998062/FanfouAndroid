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

package sinyuk.com.twitter.api

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query
import sinyuk.com.common.api.ApiResponse
import sinyuk.com.common.realm.model.Player

/**
 * Created by sinyuk on 2018/5/14.
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
@Suppress("FunctionName", "unused")
interface TwitterAPI {
    companion object {
        const val STATUS_ONE_PAGE = 50
    }

    @GET("/1.1/account/verify_credentials.json")
    fun verifyCredentials(): LiveData<ApiResponse<Player>>


    @GET("/1.1/users/show.json")
    fun showUser(@Query("user_id") id: String? = null,
                 @Query("screen_name") screenName: String? = null): LiveData<ApiResponse<Player>>

    @GET("/1.1/friends/list.json")
    fun friends(@Query("user_id") id: String? = null,
                @Query("screen_name") screenName: String? = null,
                @Query("cursor") page: Long = -1,
                @Query("count") count: Int = 100,
                @Query("skip_status") skipStatus: Boolean = true): LiveData<ApiResponse<Users>>


}