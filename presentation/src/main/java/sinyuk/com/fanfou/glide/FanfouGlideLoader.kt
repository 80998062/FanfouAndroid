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

package sinyuk.com.fanfou.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import com.bumptech.glide.request.target.Target
import sinyuk.com.common.realm.model.Photo
import java.io.InputStream

/**
 * Created by sinyuk on 2018/5/14.
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
class FanfouGlideLoader constructor(@Suppress("CanBeParameter")
                                    private val urlLoader: ModelLoader<GlideUrl, InputStream>)
    : BaseGlideUrlLoader<Photo>(urlLoader) {

    //        "url": "http://fanfou.com/photo/8bhOK6p7iR8",
    //        "imageurl": "http://photo1.fanfou.com/v1/ff/n0/0d/th/8h_350421.gif@200w_200h_1l.jpg",
    //        "thumburl": "http://photo1.fanfou.com/v1/ff/n0/0d/th/8h_350421.gif@120w_120h_1l.jpg",
    //        "largeurl": "http://photo1.fanfou.com/v1/ff/n0/0d/th/8h_350421.gif@596w_1l.gif"

    override fun getUrl(model: Photo?, width: Int, height: Int, options: Options?) =
            if (model == null) {
                null
            } else if (width == Target.SIZE_ORIGINAL || height == Target.SIZE_ORIGINAL) {
                Photo.originalUrl(model)
            } else if (urlBySize(model, width, height) != null) {
                urlBySize(model, (width * 0.5).toInt(), (height * 0.5).toInt())
            } else if (width < 596 || height < 596) {
                model.largeurl
            } else if (width < 200 || height < 200) {
                model.imageurl
            } else if (width < 120 || height < 120) {
                model.thumburl
            } else {
                model.largeurl
            }

    private fun urlBySize(model: Photo, width: Int, height: Int): String? {
        val valid = when {
            model.largeurl != null -> model.largeurl
            model.thumburl != null -> model.thumburl
            else -> model.imageurl
        } ?: return null

        val it = if (width > height) width else height
        val ext = if (valid.endsWith("gif", true)) "gif" else "jpg"
        return valid.split("@").first() + "@" + it + "w_1l." + ext
    }


    override fun handles(model: Photo) = true


    class Factory : ModelLoaderFactory<Photo, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory) =
                FanfouGlideLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))


        override fun teardown() {
            // TODO
        }
    }
}

