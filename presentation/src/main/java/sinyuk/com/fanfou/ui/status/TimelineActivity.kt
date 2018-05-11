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
import android.os.Bundle
import kotlinx.android.synthetic.main.timeline_activity.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.domain.States
import sinyuk.com.fanfou.domain.api.TIMELINE_HOME
import sinyuk.com.fanfou.ext.addFragment
import sinyuk.com.fanfou.ext.obtainViewModel
import sinyuk.com.fanfou.ui.FanfouViewModelFactory
import sinyuk.com.fanfou.ui.base.AbstractActivity
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
class TimelineActivity : AbstractActivity() {

    @Suppress("MemberVisibilityCanBePrivate")
    @Inject
    lateinit var factory: FanfouViewModelFactory

    private val statusesViewModel by lazy { obtainViewModel(factory, StatusesViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timeline_activity)

        configuration()
        setup()
    }

    private fun configuration() {
        val statusesView = if (intent?.extras != null) {
            val path: String = intent.extras.getString("path")
            val id: String? = intent.extras.getString("id")
            statusesViewModel.setRelativeUrl(path, id)
            StatusesView.newInstance(path, id)
        } else {
            statusesViewModel.setRelativeUrl(TIMELINE_HOME)
            StatusesView.newInstance(TIMELINE_HOME)
        }
        addFragment(R.id.fragment_container, statusesView, false)
    }

    private fun setup() {
        back.setOnClickListener { finish() }
        statusesViewModel.refreshState.observe(this@TimelineActivity,
                Observer {
                    swipeRefreshLayout.isRefreshing = it?.states == States.LOADING
                })
        swipeRefreshLayout.setOnRefreshListener { statusesViewModel.refresh() }
    }


}