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

package sinyuk.com.fanfou.ui

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ArrayRes
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuObject
import com.yalantis.contextmenu.lib.MenuParams
import sinyuk.com.fanfou.R


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

fun getMenuDialog(context: Context, @ArrayRes iconsId: Int, @ArrayRes titlesId: Int,
                  animationDelay: Int = 0): ContextMenuDialogFragment {
    val params = getMenuParams(context, iconsId, titlesId, animationDelay)
    return ContextMenuDialogFragment.newInstance(params)
}

private fun getMenuParams(context: Context, @ArrayRes iconsId: Int, @ArrayRes titlesId: Int,
                          animationDelay: Int): MenuParams {
    val menuParams = MenuParams()
    menuParams.actionBarSize = context.resources.getDimension(R.dimen.action_bar_height).toInt()
    menuParams.menuObjects = getMenuObjs(context, iconsId, titlesId)
    menuParams.isClosableOutside = true
    menuParams.animationDelay = animationDelay
    return menuParams
}

private fun getMenuObjs(context: Context, @ArrayRes iconsId: Int, @ArrayRes titlesId: Int)
        : MutableList<MenuObject> {
    val menuObjects = mutableListOf<MenuObject>()
    val icons = context.resources.getIntArray(iconsId)
    val titles = context.resources.getStringArray(titlesId)
    for (i in 0 until (icons.size + 1)) {
        val obj = MenuObject()
        if (i == 0) {
            obj.title = context.getString(R.string.action_close)
            obj.resource = R.drawable.ic_close
        } else {
            obj.title = titles[i - 1]
            obj.resource = icons[i - 1]
        }
        obj.setBgDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.windowBackground)))
        obj.menuTextAppearanceStyle = R.style.context_menu_text
        obj.scaleType = ImageView.ScaleType.CENTER_INSIDE
        menuObjects.add(obj)
    }
    return menuObjects
}