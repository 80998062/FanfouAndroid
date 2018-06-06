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


package sinyuk.com.fanfou.api

import android.arch.lifecycle.LiveData
import retrofit2.http.GET
import sinyuk.com.common.api.ApiResponse
import sinyuk.com.common.realm.model.Player

/**
 * Created by sinyuk on 2017/11/28.
 *
 */
@Suppress("FunctionName", "unused")
interface FanfouAPI {


//    @GET("statuses/{path}.json?format=html")
//    fun statuses_from_path(@Path("path") path: String,
//                           @Query("count") count: Int = STATUS_ONE_PAGE,
//                           @Query("since_id") since: String? = null,
//                           @Query("max_id") max: String? = null,
//                           @Query("id") id: String? = null,
//                           @Query("page") page: Int? = null): Call<MutableList<Status>>

    @GET("account/verify_credentials.json")
    fun verifyCredentials(): LiveData<ApiResponse<Player>>


//    @GET("/statuses/user_timeline.json")
//    fun fetch_latest_status(@Query("id") id: String,
//                            @Query("count") count: Int = 1): Call<List<Status>>
//
//    @GET("users/show.json?format=html")
//    @Deprecated("unused")
//    fun show_user(@Query("id") uniqueId: String): LiveData<ApiResponse<Player>>
//
//
//    @GET("favorites/id.json?format=html")
//    fun fetch_favorites(@Query("id") id: String? = null,
//                        @Query("count") count: Int,
//                        @Query("page") page: Int): Call<MutableList<Status>>
//
//    @POST("favorites/create/{id}.json?format=html")
//    fun createFavorite(@Path("id") id: String): Call<Status>
//
//
//    @POST("favorites/destroy/{id}.json?format=html")
//    fun deleteFavorite(@Path("id") id: String): Call<Status>

}