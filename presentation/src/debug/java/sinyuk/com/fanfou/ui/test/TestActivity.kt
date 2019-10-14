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

package sinyuk.com.fanfou.ui.test

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import kotlinx.android.synthetic.main.test_activity.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.ui.base.AbstractActivity

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
class TestActivity : AbstractActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        fanfouFriends.setOnClickListener {

        }
    }


    private fun switch(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.javaClass.simpleName)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(fragment::class.java.simpleName)
                .commit()
    }

}