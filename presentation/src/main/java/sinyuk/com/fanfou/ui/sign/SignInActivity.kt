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

package sinyuk.com.fanfou.ui.sign

import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewTreeObserver
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import kotlinx.android.synthetic.main.signin_activity.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.ext.addFragment
import sinyuk.com.fanfou.ui.base.AbstractActivity

/**
 * Created by sinyuk on 2018/5/4.
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
class SignInActivity : AbstractActivity() {

    private var signInView: SignInView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_activity)
        signInView = SignInView().apply {
            addFragment(R.id.fragment_container, this, false)
        }
        setup()
    }

    lateinit var keyboardListener: ViewTreeObserver.OnGlobalLayoutListener

    private fun setup() {
        keyboardListener = KeyboardUtil.attach(this, panel) {
            if (!it) signInView?.clearFocus()
        }

        nestedScrollView.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_CANCEL || event?.action == MotionEvent.ACTION_UP) {
                KeyboardUtil.hideKeyboard(v)
            }
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtil.detach(this, keyboardListener)
    }

}