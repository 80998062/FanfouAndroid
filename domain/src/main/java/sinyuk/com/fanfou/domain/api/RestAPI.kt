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
import android.support.annotation.VisibleForTesting
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
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

    @VisibleForTesting
    @GET("statuses/{path}.json")
    fun statuses_from_path(@Path("path") path: String,
                           @Query("count") count: Int = STATUS_ONE_PAGE,
                           @Query("since_id") since: String? = null,
                           @Query("max_id") max: String? = null,
                           @Query("id") id: String? = null,
                           @Query("page") page: Int? = null): Call<MutableList<Status>>

    @GET("account/verify_credentials.json")
    fun verify_credentials(): LiveData<ApiResponse<Player>>

}