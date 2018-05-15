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

package sinyuk.com.fanfou.repo

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.WorkerThread
import sinyuk.com.common.Promise
import sinyuk.com.common.api.ApiResponse
import sinyuk.com.common.room.LocalDatabase
import sinyuk.com.fanfou.api.FanfouAPI
import sinyuk.com.fanfou.api.TIMELINE_HOME
import sinyuk.com.fanfou.data.Status
import java.io.IOException

/**
 * Created by sinyuk on 2017/12/13.
 *
 *
 */
class StatusFetchTopTask(private val fanfouAPI: FanfouAPI,
                         private val db: LocalDatabase,
                         private val pageSize: Int) : Runnable {

    val livedata = MutableLiveData<Promise<MutableList<Status>>>()

    init {
        livedata.postValue(Promise.loading(null))
    }

    override fun run() {
        try {
            val response =
                    fanfouAPI.statuses_from_path(path = TIMELINE_HOME, count = pageSize).execute()
            val apiResponse = ApiResponse(response)
            if (apiResponse.isSuccessful()) {
                val data = apiResponse.body
                if (insertResultIntoDb(data) > 0) {
                    livedata.postValue(Promise.success(data))
                } else {
                    livedata.postValue(Promise.success(mutableListOf()))
                }
            } else {
                livedata.postValue(Promise.error("error code: ${response.code()}", null))
            }
        } catch (e: IOException) {
            livedata.postValue(Promise.error("error msg: ${e.message}", null))
        }
    }


    @WorkerThread
    private fun insertResultIntoDb(body: MutableList<Status>?): Int {
        if (body?.isEmpty() != false) return 0
        body.forEach { it.uniqueId = it.player?.uniqueId ?: "" }
        val size: Int
        try {
            db.beginTransaction()
            size = db.statusDao().inserts(body).size
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return size
    }

}