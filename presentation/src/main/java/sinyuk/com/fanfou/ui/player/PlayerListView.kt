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

package sinyuk.com.fanfou.ui.player

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.player_list_view.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.ui.base.AbstractFragment

/**
 * Created by sinyuk on 2018/6/10.
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
class PlayerListView : AbstractFragment() {

    lateinit var playerViewModel: PlayerViewModel

    companion object {
        fun newInstance(path: String)  = PlayerListView().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListView()
    }

    override fun lazyDo() {
        refresh()
    }

    override fun layoutId() = R.layout.player_list_view

    private var page: Int = 1
    fun refresh() {
        page = 1
        playerViewModel.loadFriends(page)
                .observe(this, Observer {
                    if (it?.isSuccessful() == true) {
                        if (it.body?.size == playerViewModel.pageCount) {
                            page++
                            adapter.setEnableLoadMore(true)
                            adapter.setOnLoadMoreListener({ loadmore() }, recyclerView)
                        } else {
                            adapter.setEnableLoadMore(false)
                        }
                        adapter.setNewData(it.body)
                    }
                })
    }


    fun loadmore() {
        playerViewModel.loadFriends(page)
                .observe(this, Observer {
                    if (it?.isSuccessful() == true) {
                        if (it.body == null) {
                            adapter.loadMoreFail()
                        } else {
                            if (it.body!!.size == playerViewModel.pageCount) {
                                page++
                                adapter.loadMoreComplete()
                            } else {
                                adapter.loadMoreEnd(false)
                            }
                            adapter.addData(it.body!!)
                        }
                    } else {
                        adapter.loadMoreFail()
                    }
                })
    }

    lateinit var adapter: PlayerAdapter
    private fun setupListView() {
        LinearLayoutManager(context).apply { recyclerView.layoutManager = this }
        adapter = PlayerAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }
}