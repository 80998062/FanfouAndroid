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

import android.graphics.drawable.TransitionDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.sinyuk.fanfou.util.linkfy.FanfouUtils
import kotlinx.android.synthetic.main.status_list_item.view.*
import kotlinx.android.synthetic.main.status_list_item_surface.view.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.domain.data.Photos
import sinyuk.com.fanfou.domain.data.Status
import sinyuk.com.fanfou.ext.dp2px
import sinyuk.com.fanfou.glide.GlideRequests


/**
 * Created by sinyuk on 2017/12/18.
 *
 * A RecyclerView ViewHolder that displays a status.
 */
class StatusItemHolder(private val view: View, private val glide: GlideRequests) : RecyclerView.ViewHolder(view) {

    fun bind(status: Status) {
        view.swipeLayout.isRightSwipeEnabled = true
        view.swipeLayout.isClickToClose = true
        glide.asDrawable().avatar().load(status.author?.profileImageUrl).into(view.surfaceView.avatar)

        // Clear background
        view.surfaceView.screenName.background = null
        view.surfaceView.createdAt.background = null
        view.surfaceView.content.background = null
        view.surfaceView.screenName.text = status.author?.screenName
//        view.createdAt.text = DateUtils.getTimeAgo(view.context, status.createdAt)

        /**
         * code about imageView
         */
        // play animated GIFs whilst touched
        view.surfaceView.image.setOnTouchListener(View.OnTouchListener { v, event ->
            // check if it's an event we care about, else bail fast
            val action = event?.action
            if (!(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_CANCEL)) {
                return@OnTouchListener false
            }
            // get the image and check if it's an animated GIF
            val drawable = (v as ImageView).drawable
            var gif: GifDrawable? = null
            if (drawable == null) return@OnTouchListener false
            if (drawable is GifDrawable) {
                gif = drawable
            } else if (drawable is TransitionDrawable) {
                // we fade in images on load which uses a TransitionDrawable; check its layers
                for (i in 0 until drawable.numberOfLayers) {
                    if (drawable.getDrawable(i) is GifDrawable) {
                        gif = drawable.getDrawable(i) as GifDrawable
                        break
                    }
                }
            }

            if (gif == null) return@OnTouchListener false

            // GIF found, start/stop it on press/lift
            when (action) {
                MotionEvent.ACTION_DOWN -> gif.start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> gif.stop()
            }
            false
        })

        val url = status.photos?.size(dp2px(view.context, Photos.SMALL_SIZE))

        if (url == null) {
            view.surfaceView.image.setOnClickListener(null)
            view.surfaceView.image.visibility = View.GONE
            glide.clear(view.surfaceView.image)
        } else {
            view.image.visibility = View.VISIBLE
            glide.asDrawable().image(view.context).load(url).into(view.surfaceView.image)
        }

        FanfouUtils.parseAndSetText(view.surfaceView.content, status.text)

        view.surfaceView.setOnClickListener {}
    }

    fun clear() {
        view.swipeLayout.isRightSwipeEnabled = false
        view.surfaceView.avatar.setOnClickListener(null)
        view.surfaceView.screenName.setBackgroundColor(ContextCompat.getColor(view.context, R.color.textColorHint))
        view.surfaceView.createdAt.setBackgroundColor(ContextCompat.getColor(view.context, R.color.textColorHint))
        view.surfaceView.content.setBackgroundColor(ContextCompat.getColor(view.context, R.color.textColorHint))
        view.surfaceView.screenName.text = null
        view.surfaceView.createdAt.text = null
        view.surfaceView.content.text = null
        glide.clear(view.surfaceView.avatar)
        glide.clear(view.surfaceView.image)
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): StatusItemHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.status_list_item, parent, false)
            return StatusItemHolder(view, glide)
        }
    }
}



