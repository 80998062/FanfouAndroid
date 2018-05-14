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

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by sinyuk on 2018/5/8.
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
@Suppress("unused")
@GlideExtension
object MyGlideExt {
    @SuppressLint("CheckResult")
    @GlideOption
    @JvmStatic
    fun avatar(options: RequestOptions) = options.optionalCircleCrop()


    @SuppressLint("CheckResult")
    @GlideOption
    @JvmStatic
    fun thumb(options: RequestOptions, context: Context) =
            options.centerCrop()
                    .apply(RequestOptions.bitmapTransform(
                            RoundedCornersTransformation(dp2px(context, 4f), 0)))


    @SuppressLint("CheckResult")
    @GlideOption
    @JvmStatic
    fun large(options: RequestOptions, context: Context) =
            options.centerCrop()
                    .apply(RequestOptions.bitmapTransform(
                            RoundedCornersTransformation(dp2px(context, 8f), 0)))


    private fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}