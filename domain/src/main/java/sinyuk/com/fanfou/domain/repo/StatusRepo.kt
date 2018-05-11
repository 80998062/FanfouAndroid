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
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.VisibleForTesting
import android.support.annotation.WorkerThread
import sinyuk.com.fanfou.domain.AppExecutors
import sinyuk.com.fanfou.domain.Promise
import sinyuk.com.fanfou.domain.ROOM_IN_DISK
import sinyuk.com.fanfou.domain.ROOM_IN_MEMORY
import sinyuk.com.fanfou.domain.api.RestAPI
import sinyuk.com.fanfou.domain.data.Player
import sinyuk.com.fanfou.domain.data.Status
import sinyuk.com.fanfou.domain.room.LocalDatabase
import sinyuk.com.fanfou.domain.usecase.StatusUsecase
import sinyuk.com.fanfou.domain.utils.Listing
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/5/9.
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
class StatusRepo @Inject constructor(private val restAPI: RestAPI,
                                     private val appExecutors: AppExecutors,
                                     @Named(ROOM_IN_MEMORY) private val memory: LocalDatabase,
                                     @Named(ROOM_IN_DISK) private val disk: LocalDatabase) : StatusUsecase {
    override fun home(count: Int): Listing<Status> {
        val factory = disk.statusDao().home()

        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = StatusBoundaryCallback(
                webservice = restAPI,
                handleResponse = this::insertResultIntoDb,
                executors = appExecutors,
                networkPageSize = count)

        val config = PagedList.Config.Builder()
                .setPageSize(count)
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Math.min(count / 2, 10))
                .setInitialLoadSizeHint(count)

        val builder = LivePagedListBuilder(factory, config.build())
                .setBoundaryCallback(boundaryCallback)
                .setFetchExecutor(appExecutors.diskIO())

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val trigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(trigger, { fetchTop(count) })

        return Listing(
                pagedList = builder.build(),
                networkState = boundaryCallback.networkState,
                retry = { boundaryCallback.helper.retryAllFailed() },
                refresh = { trigger.value = null },
                refreshState = refreshState
        )
    }


    fun fetchTop(pageSize: Int): LiveData<Promise<MutableList<Status>>> {
        TODO()
    }


    @Suppress("MemberVisibilityCanBePrivate")
    @WorkerThread
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertResultIntoDb(body: MutableList<Status>?) {
        if (body?.isNotEmpty() == true) {
            val players = mutableListOf<Player>()
            body.forEach {
                it.uniqueId = it.player?.uniqueId ?: ""
                it.player?.let { players.add(it) }
            }
            disk.beginTransaction()
            try {
                disk.playerDao().inserts(players)
                disk.statusDao().inserts(body)
                disk.setTransactionSuccessful()
            } finally {
                disk.endTransaction()
            }
        }
    }

}