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

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If using the TwitterLoginButton in a Fragment, use the following steps instead.
        // Inside the Activity hosting the Fragment, pass the result from the Activity to the Fragment.
        // Pass the activity result to the fragment, which will then pass the result to the login
        // button.
        signInView?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME
                || keyCode == KeyEvent.FLAG_CANCELED) {
            if (signInView?.onBackpress() == true) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}