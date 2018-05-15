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

import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import kotlinx.android.synthetic.main.statuses_view.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.TimberDelegate
import sinyuk.com.common.NetworkState
import sinyuk.com.fanfou.data.Status
import sinyuk.com.common.isOnline
import sinyuk.com.fanfou.ext.obtainViewModelFromActivity
import sinyuk.com.fanfou.glide.GlideApp
import sinyuk.com.fanfou.injectors.Injectable
import sinyuk.com.fanfou.rest.ConnectionModel
import sinyuk.com.fanfou.ui.FanfouViewModelFactory
import sinyuk.com.fanfou.ui.base.AbstractFragment
import javax.inject.Inject

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
class StatusesView : AbstractFragment(), Injectable {
    companion object {
        fun newInstance(path: String, uniqueId: String? = null) = StatusesView()
                .apply {
                    arguments = Bundle().apply {
                        putString("path", path)
                        uniqueId?.let { this.putString("uniqueId", it) }
                    }
                }
    }

    override fun layoutId() = R.layout.statuses_view

    @Suppress("MemberVisibilityCanBePrivate")
    @Inject
    lateinit var factory: FanfouViewModelFactory

    private val statusesViewModel by lazy { obtainViewModelFromActivity(factory, StatusesViewModel::class.java) }

    private var connectState: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("connectState", connectState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("IfThenToElvis")
        connectState = if (savedInstanceState == null) {
            isOnline(context!!.applicationContext)
        } else {
            savedInstanceState.getBoolean("connectState", false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        // Cannot add the same observer with different lifeCycles! So use activity here.
        ConnectionModel.livedata(context!!.applicationContext).observe(activity!!,
                Observer { if (connectState != it?.isConnected) performRefresh() })

    }

    private fun performRefresh() {
//        statusesViewModel.refresh()
    }

    private lateinit var adapter: StatusPagedListAdapter

    private fun setup() {
        recyclerView.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL)
                KeyboardUtil.hideKeyboard(view)
            return@setOnTouchListener false
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
                .also {
                    it.initialPrefetchItemCount = 10
                    it.isItemPrefetchEnabled = true
                }
        recyclerView.setHasFixedSize(true)

        val path: String = arguments!!.getString("path")
        TimberDelegate.tag("StatusesView").d("StatusPagedListAdapter")
        adapter = StatusPagedListAdapter(
                GlideApp.with(this), { statusesViewModel.retry() }, path)

        val imageWidthPixels = resources.getDimensionPixelSize(R.dimen.status_thumb_size)
        val modelPreloader = StatusPagedListAdapter.StatusPreloadProvider(adapter, context!!, imageWidthPixels)
        val sizePreloader = FixedPreloadSizeProvider<Status>(imageWidthPixels, imageWidthPixels)
        val preloader = RecyclerViewPreloader<Status>(Glide.with(this), modelPreloader, sizePreloader, 10)
        recyclerView.addOnScrollListener(preloader)
        recyclerView.adapter = adapter
        statusesViewModel.statuses.observe(this, pagedListConsumer)
        statusesViewModel.networkState.observe(this, networkConsumer)
    }

    private val pagedListConsumer = Observer<PagedList<Status>> {
        // Preserves the user's scroll position if items are inserted outside the viewable area:
        adapter.submitList(it)
//        recyclerView.post { recyclerView.layoutManager.onRestoreInstanceState(recyclerViewState) }
    }

    private val networkConsumer = Observer<NetworkState> {
        adapter.setNetworkState(it)
    }
}