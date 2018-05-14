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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sinyuk.com.fanfou.R
import sinyuk.com.common.NetworkState

/**
 * Created by sinyuk on 2017/12/18.
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
/**

 */
class NetworkStateItemViewHolder(view: View, private val retryCallback: () -> Unit)
    : RecyclerView.ViewHolder(view) {


    init {
//        retry.setOnClickListener {
//            retryCallback()
//        }
    }

    fun bind(networkState: NetworkState?) {}


    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view, retryCallback)
        }
    }
}