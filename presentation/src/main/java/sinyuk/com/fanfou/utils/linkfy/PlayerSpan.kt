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

package com.sinyuk.fanfou.util.linkfy

import android.content.res.ColorStateList
import android.view.View
import sinyuk.com.fanfou.utils.linkfy.TouchableUrlSpan


/**
 * A span for marking up a fanfou player
 */
class PlayerSpan
/**
 * Instantiates a new Player span.
 *
 * @param uniqueId                 the uniqueId
 * @param textColor              the text color
 * @param pressedBackgroundColor the pressed background color
 */
(url:String,private val uniqueId: String?, textColor: ColorStateList, pressedBackgroundColor: Int) : TouchableUrlSpan(url, textColor, pressedBackgroundColor) {

    override fun onClick(view: View) {
    }
}
