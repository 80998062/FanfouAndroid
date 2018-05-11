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

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.text.TextPaint
import android.text.style.ForegroundColorSpan


/**
 * An extension to [ForegroundColorSpan] which allows updating the color or alpha component.
 * Note that Spans cannot invalidate themselves so consumers must ensure that the Spannable is
 * refreshed themselves.
 */
class TextColorSpan : ForegroundColorSpan {

    @ColorInt
    @get:ColorInt
    var color: Int = 0

    constructor(color: Int) : super(color) {
        this.color = color
    }

    fun setAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
        color = modifyAlpha(color, alpha)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.color)
    }

    protected constructor(`in`: Parcel) : super(`in`) {
        this.color = `in`.readInt()
    }

    companion object {

        fun modifyAlpha(@ColorInt color: Int, @IntRange(from = 0L, to = 255L) alpha: Int): Int {
            return color and 16777215 or (alpha shl 24)
        }

        fun modifyAlpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
            return modifyAlpha(color, (255.0f * alpha).toInt())
        }

        val CREATOR: Parcelable.Creator<TextColorSpan> = object : Parcelable.Creator<TextColorSpan> {
            override fun createFromParcel(source: Parcel): TextColorSpan {
                return TextColorSpan(source)
            }

            override fun newArray(size: Int): Array<TextColorSpan?> {
                return arrayOfNulls(size)
            }
        }
    }
}
