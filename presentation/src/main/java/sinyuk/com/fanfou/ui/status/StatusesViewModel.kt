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

package sinyuk.com.fanfou.ui.status

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sinyuk.com.fanfou.domain.api.TIMELINE_HOME
import sinyuk.com.fanfou.domain.data.Status
import sinyuk.com.fanfou.domain.repo.StatusRepo
import sinyuk.com.fanfou.domain.utils.Listing
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by sinyuk on 2018/5/10.
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
class StatusesViewModel @Inject constructor(private val statusRepo: StatusRepo) : ViewModel() {

    data class RelativeUrl(val path: String, val uniqueId: String? = null, val query: String? = null)

    private var relativeUrl: MutableLiveData<RelativeUrl> = MutableLiveData()

    fun setRelativeUrl(path: String, uniqueId: String? = null, query: String? = null) {
        setRelativeUrl(RelativeUrl(path, uniqueId, query))
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setRelativeUrl(url: RelativeUrl) = if (url == relativeUrl.value) {
        false
    } else {
        relativeUrl.value = url
        true
    }

    private val repoResult: LiveData<Listing<Status>> = Transformations.map(relativeUrl, {
        if (TIMELINE_HOME == it.path) {
            statusRepo.home(50)
        } else {
            statusRepo.fetch(it.uniqueId, it.path, 50)
        }
    })

    val statuses = Transformations.switchMap(repoResult, { it.pagedList })!!
    val networkState = Transformations.switchMap(repoResult, { it.networkState })!!
    val refreshState = Transformations.switchMap(repoResult, { it.refreshState })!!


    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }
}