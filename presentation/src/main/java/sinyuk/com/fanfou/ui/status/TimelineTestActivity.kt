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

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.timeline_test_activity.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.domain.api.TIMELINE_USER
import sinyuk.com.fanfou.domain.repo.StatusRepo
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
class TimelineTestActivity : AbstractActivity() {
    @Inject
    lateinit var statusRepo: StatusRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timeline_test_activity)


        homeTimeline.setOnClickListener {
            val i = Intent(this@TimelineTestActivity, TimelineActivity::class.java)
            startActivity(i)
        }

        userTimeline.setOnClickListener {
            val i = Intent(this@TimelineTestActivity, TimelineActivity::class.java)
            i.putExtras(Bundle().apply { putString("path", TIMELINE_USER) })
            startActivity(i)
        }
    }
}