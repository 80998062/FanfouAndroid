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
import android.view.View
import kotlinx.android.synthetic.main.player_view.*
import kotlinx.android.synthetic.main.player_view_head.view.*
import sinyuk.com.fanfou.R
import sinyuk.com.common.States
import sinyuk.com.common.realm.model.Player
import sinyuk.com.fanfou.ext.obtainViewModel
import sinyuk.com.fanfou.glide.GlideApp
import sinyuk.com.fanfou.injectors.Injectable
import sinyuk.com.fanfou.rest.ConnectionModel
import sinyuk.com.fanfou.ui.FanfouViewModelFactory
import sinyuk.com.fanfou.ui.base.AbstractFragment
import javax.inject.Inject

/**
 * Created by sinyuk on 2018/5/7.
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
class PlayerView : AbstractFragment(), Injectable {

    companion object {
        fun newInstance(uniqueId: String? = null) = PlayerView().apply {
            arguments = Bundle().apply {
                uniqueId?.let { this.putString("uniqueId", it) }
            }
        }
    }

    override fun layoutId() = R.layout.player_view

    lateinit var uniqueId: String

    @Inject
    lateinit var factory: FanfouViewModelFactory

    private val playerViewModel: PlayerViewModel by lazy { obtainViewModel(factory, PlayerViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()
        if (arguments?.containsKey("uniqueId") == true) {
            uniqueId = arguments!!.getString("uniqueId")
            render()
        } else {
            TODO()
        }

        ConnectionModel.livedata(context!!)
                .observe(this@PlayerView, Observer {
                    if (it?.isConnected == true) render()
                })
    }


    private fun ui() {
        back.setOnClickListener { activity?.finish() }
    }

    private fun render() {
        playerViewModel.fetch(uniqueId)
                .observe(this@PlayerView, Observer {
                    if (it?.states == States.SUCCESS) renderPlayer(it.data)
                })
    }

    private fun renderPlayer(player: Player?) {
        player?.let {
            head.screenName.text = it.screenName
            head.introduce.text = it.description
            GlideApp.with(this).asDrawable().avatar().load(it.profileImageUrl).into(head.avatar)
        }
    }
}