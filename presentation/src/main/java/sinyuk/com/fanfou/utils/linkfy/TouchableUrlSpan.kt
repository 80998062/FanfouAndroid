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

package sinyuk.com.fanfou.utils.linkfy

import android.content.res.ColorStateList
import android.os.Parcel
import android.os.Parcelable
import android.text.TextPaint
import android.text.style.URLSpan

/**
 * An extension to URLSpan which changes it's background & foreground color when clicked.
 *
 *
 * Derived from http://stackoverflow.com/a/20905824
 */
open class TouchableUrlSpan : URLSpan {
    private var isPressed: Boolean = false
    private var normalTextColor: Int = 0
    private var pressedTextColor: Int = 0
    private var pressedBackgroundColor: Int = 0

    /**
     * Instantiates a new Touchable url span.
     *
     * @param url                    the url
     * @param textColor              the text color
     * @param pressedBackgroundColor the pressed background color
     */
    constructor(url: String, textColor: ColorStateList, pressedBackgroundColor: Int) : super(url) {
        this.normalTextColor = textColor.defaultColor
        this.pressedTextColor = textColor.getColorForState(STATE_PRESSED, normalTextColor)
        this.pressedBackgroundColor = pressedBackgroundColor
    }

    /**
     * Sets pressed.
     *
     * @param isPressed the is pressed
     */
    fun setPressed(isPressed: Boolean) {
        this.isPressed = isPressed
    }

    override fun updateDrawState(drawState: TextPaint) {
        drawState.color = if (isPressed) pressedTextColor else normalTextColor
        drawState.bgColor = if (isPressed) pressedBackgroundColor else 0
        drawState.isUnderlineText = !isPressed
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(if (this.isPressed) 1.toByte() else 0.toByte())
        dest.writeInt(this.normalTextColor)
        dest.writeInt(this.pressedTextColor)
        dest.writeInt(this.pressedBackgroundColor)
    }

    private constructor(`in`: Parcel) : super(`in`) {
        this.isPressed = `in`.readByte().toInt() != 0
        this.normalTextColor = `in`.readInt()
        this.pressedTextColor = `in`.readInt()
        this.pressedBackgroundColor = `in`.readInt()
    }

    companion object {

        private val STATE_PRESSED = intArrayOf(android.R.attr.state_pressed)

        /**
         * The constant CREATOR.
         */
        val CREATOR: Parcelable.Creator<TouchableUrlSpan> = object : Parcelable.Creator<TouchableUrlSpan> {
            override fun createFromParcel(source: Parcel): TouchableUrlSpan {
                return TouchableUrlSpan(source)
            }

            override fun newArray(size: Int): Array<TouchableUrlSpan?> {
                return arrayOfNulls(size)
            }
        }
    }
}