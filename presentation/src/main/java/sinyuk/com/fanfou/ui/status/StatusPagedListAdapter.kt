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

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import com.daimajia.swipe.interfaces.SwipeAdapterInterface
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface
import com.daimajia.swipe.util.Attributes
import sinyuk.com.fanfou.R
import sinyuk.com.common.NetworkState
import sinyuk.com.fanfou.api.TIMELINE_FAVORITES
import sinyuk.com.common.realm.model.Status
import sinyuk.com.fanfou.glide.GlideApp
import sinyuk.com.fanfou.glide.GlideRequests
import java.util.*

/**
 * Created by sinyuk on 2017/12/18.
 *
 * Adapter implementation that shows status.
 */
class StatusPagedListAdapter(
        private val glide: GlideRequests,
        private val retryCallback: () -> Unit,
        val path: String?) :
        PagedListAdapter<Status, RecyclerView.ViewHolder>(StatusComparator()), SwipeItemMangerInterface, SwipeAdapterInterface {
    private var networkState: NetworkState? = null
    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED
    override fun getItemViewType(position: Int) = if (hasExtraRow() && position == itemCount - 1) {
        R.layout.network_state_item
    } else {
        R.layout.status_list_item
    }


    override fun getItemCount() = super.getItemCount() + if (hasExtraRow()) 1 else 0

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.status_list_item -> StatusItemHolder.create(parent, glide)
        R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
        else -> throw IllegalArgumentException("unknown view type $viewType")
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            TODO()
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.status_list_item -> {
                holder as StatusItemHolder
                if (getItem(position) == null) {
                    // Null defines a placeholder item - PagedListAdapter will automatically invalidate
                    // this row when the actual object is loaded from the database
                    holder.clear()
                } else {
                    val status = getItem(position)!!
                    // path == favorited 返回的数据 favorited == false
                    status.favorited == status.favorited || TIMELINE_FAVORITES == path
                    holder.bind(status)

                    holder.itemView.swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {
                        override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {

                        }

                        override fun onStartOpen(layout: SwipeLayout?) {
                        }

                        override fun onStartClose(layout: SwipeLayout?) {
                        }

                        override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                        }

                        override fun onClose(layout: SwipeLayout?) {
                        }

                        override fun onOpen(layout: SwipeLayout?) {
                            mItemManger.closeAllExcept(layout)
                        }
                    })
                }
            }
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    class StatusComparator : DiffUtil.ItemCallback<Status>() {
        override fun areContentsTheSame(oldItem: Status, newItem: Status) = newItem.favorited == oldItem.favorited && (oldItem.author?.screenName == oldItem.author?.screenName && oldItem.author?.profileImageUrl == oldItem.author?.profileImageUrl && oldItem.author?.birthday == oldItem.author?.birthday)
        override fun areItemsTheSame(oldItem: Status, newItem: Status) = oldItem.id == newItem.id
    }

    /**
     * Swipe implementing
     */

    private var mItemManger = SwipeItemRecyclerMangerImpl(this)

    override fun openItem(position: Int) {
        mItemManger.openItem(position)
    }

    override fun closeItem(position: Int) {
        mItemManger.closeItem(position)
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        mItemManger.closeAllExcept(layout)
    }

    override fun closeAllItems() {
        mItemManger.closeAllItems()
    }

    override fun getOpenItems(): List<Int> {
        return mItemManger.openItems
    }

    override fun getOpenLayouts(): List<SwipeLayout> {
        return mItemManger.openLayouts
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return mItemManger.isOpen(position)
    }

    override fun getMode(): Attributes.Mode {
        return mItemManger.mode
    }

    override fun setMode(mode: Attributes.Mode) {
        mItemManger.mode = mode
    }

    override fun getSwipeLayoutResourceId(position: Int): Int = R.id.swipeLayout


    /**
     *
     */
    class StatusPreloadProvider constructor(private val adapter: StatusPagedListAdapter, private val context: Context, private val imageWidthPixels: Int) : ListPreloader.PreloadModelProvider<Status> {

        override fun getPreloadRequestBuilder(item: Status): RequestBuilder<*>? {
            return GlideApp.with(context).asDrawable().thumb(context)
                    .load(item.photos).override(imageWidthPixels, imageWidthPixels)
        }

        override fun getPreloadItems(position: Int): MutableList<Status> =
                if (adapter.currentList?.isNotEmpty() == true
                        && position < adapter.currentList?.size ?: 0) {
                    val status = adapter.currentList!![position]
                    if (status == null) {
                        Collections.emptyList<Status>()
                    } else {
                        if (status.photos == null) {
                            Collections.emptyList<Status>()
                        } else {
                            Collections.singletonList(status)
                        }
                    }

                } else {
                    Collections.emptyList<Status>()
                }
    }
}