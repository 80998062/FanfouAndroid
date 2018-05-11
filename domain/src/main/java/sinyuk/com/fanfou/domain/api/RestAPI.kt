/*
 *
 *  * Apache License
 *  *
 *  * Copyright [2017] Sinyuk
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */


package sinyuk.com.fanfou.domain.api

import android.arch.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sinyuk.com.fanfou.domain.data.Player
import sinyuk.com.fanfou.domain.data.Status

/**
 * Created by sinyuk on 2017/11/28.
 *
 */
@Suppress("FunctionName")
interface RestAPI {
    companion object {
        const val STATUS_ONE_PAGE = 50
    }

    @GET("statuses/{path}.json")
    fun statuses_from_path(@Path("path") path: String,
                           @Query("count") count: Int = STATUS_ONE_PAGE,
                           @Query("since_id") since: String? = null,
                           @Query("max_id") max: String? = null,
                           @Query("id") id: String? = null,
                           @Query("page") page: Int? = null): Call<MutableList<Status>>

    @GET("account/verify_credentials.json")
    fun verify_credentials(): LiveData<ApiResponse<Player>>


    @GET("/statuses/user_timeline.json")
    fun fetch_latest_status(@Query("id") id: String,
                            @Query("count") count: Int = 1): Call<List<Status>>

    @GET("users/show.json?format=html")
    @Deprecated("unused")
    fun show_user(@Query("id") uniqueId: String): LiveData<ApiResponse<Player>>

    fun photos(count: Int, id: String?, max: String? = null): Call<MutableList<Status>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}