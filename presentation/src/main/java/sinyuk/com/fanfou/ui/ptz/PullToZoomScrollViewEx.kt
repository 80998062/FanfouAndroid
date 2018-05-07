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

@file:Suppress("unused")

package sinyuk.com.fanfou.ui.ptz

import android.content.Context
import android.content.res.TypedArray
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import sinyuk.com.fanfou.domain.R

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2014/11/10  14:25.
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2014/11/10        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
class PullToZoomScrollViewEx @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : PullToZoomBase<ScrollView>(context, attrs) {
    private var isCustomHeaderHeight = false
    private var mHeaderContainer: FrameLayout? = null
    private var mRootContainer: LinearLayout? = null
    private var mContentView: View? = null
    private var mHeaderHeight: Int = 0
    private val mScalingRunnable: ScalingRunnable?

    /**
     * 是否显示headerView
     */
    override var isHideHeader: Boolean
        get() = super.isHideHeader
        set(isHideHeader) {
            if (isHideHeader != isHideHeader && mHeaderContainer != null) {
                super.isHideHeader = isHideHeader
                if (isHideHeader) {
                    mHeaderContainer!!.visibility = View.GONE
                } else {
                    mHeaderContainer!!.visibility = View.VISIBLE
                }
            }
        }

    override val isReadyForPullStart: Boolean
        get() = mRootView?.scrollY == 0

    init {
        mScalingRunnable = ScalingRunnable()
        (mRootView as InternalScrollView).setOnScrollViewChangedListener(object : OnScrollViewChangedListener {
            override fun onInternalScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int) {
                if (isPullToZoomEnabled && isParallax) {
                    val rootScrollY = mRootView?.scrollY ?: 0
                    val f = (mHeaderHeight - mHeaderContainer!!.bottom + rootScrollY).toFloat()
                    if (f > 0.0f && f < mHeaderHeight) {
                        val i = (0.65 * f).toInt()
                        mHeaderContainer!!.scrollTo(0, -i)
                    } else if (mHeaderContainer!!.scrollY != 0) {
                        mHeaderContainer!!.scrollTo(0, 0)
                    }
                }
            }
        })
    }

    override fun pullHeaderToZoom(newScrollValue: Int) {
        if (mScalingRunnable != null && !mScalingRunnable.isFinished) {
            mScalingRunnable.abortAnimation()
        }

        val localLayoutParams = mHeaderContainer!!.layoutParams
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight
        mHeaderContainer!!.layoutParams = localLayoutParams

        if (isCustomHeaderHeight) {
            val zoomLayoutParams = mZoomView?.layoutParams
            zoomLayoutParams?.let {
                it.height = Math.abs(newScrollValue) + mHeaderHeight
                mZoomView?.layoutParams = it
            }

        }
    }

    @Suppress("unused")
    fun setHeaderView(headerView: View?) {
        if (headerView != null) {
            mHeaderView = headerView
            updateHeaderView()
        }
    }

    @Suppress("unused")
    fun setZoomView(zoomView: View?) {
        if (zoomView != null) {
            mZoomView = zoomView
            updateHeaderView()
        }
    }

    private fun updateHeaderView() {
        if (mHeaderContainer != null) {
            mHeaderContainer!!.removeAllViews()
            if (mZoomView != null) mHeaderContainer!!.addView(mZoomView)
            if (mHeaderView != null) mHeaderContainer!!.addView(mHeaderView)

        }
    }

    @Suppress("unused")
    fun setScrollContentView(contentView: View?) {
        if (contentView != null) {
            if (mContentView != null) mRootContainer!!.removeView(mContentView)
            mContentView = contentView
            mRootContainer!!.addView(mContentView)
        }
    }

    override fun createRootView(context: Context, attrs: AttributeSet?): ScrollView {
        val scrollView = InternalScrollView(context, attrs)
        scrollView.id = R.id.scrollview
        return scrollView
    }

    override fun smoothScrollToTop() {
        mScalingRunnable!!.startAnimation(200L)
    }

    override fun handleStyledAttributes(a: TypedArray?) {
        mRootContainer = LinearLayout(context)
        mRootContainer!!.orientation = LinearLayout.VERTICAL
        mHeaderContainer = FrameLayout(context)

        if (mZoomView != null) {
            mHeaderContainer!!.addView(mZoomView)
        }
        if (mHeaderView != null) {
            mHeaderContainer!!.addView(mHeaderView)
        }
        val contentViewResId = a!!.getResourceId(R.styleable.PullToZoomView_contentView, 0)
        if (contentViewResId > 0) {
            val mLayoutInflater = LayoutInflater.from(context)
            mContentView = mLayoutInflater.inflate(contentViewResId, null, false)
        }

        mRootContainer!!.addView(mHeaderContainer)
        if (mContentView != null) {
            mRootContainer!!.addView(mContentView)
        }

        mRootContainer!!.clipChildren = false
        mHeaderContainer!!.clipChildren = false

        mRootView?.addView(mRootContainer)
    }

    @Suppress("unused")
            /**
             * 设置HeaderView高度
             *
             * @param width
             * @param height
             */
    fun setHeaderViewSize(width: Int, height: Int) {
        if (mHeaderContainer != null) {
            var localObject: Any? = mHeaderContainer!!.layoutParams
            if (localObject == null) {
                localObject = ViewGroup.LayoutParams(width, height)
            }
            (localObject as ViewGroup.LayoutParams).width = width
            localObject.height = height
            mHeaderContainer!!.layoutParams = localObject
            mHeaderHeight = height
            isCustomHeaderHeight = true
        }
    }

    @Suppress("unused")
            /**
             * 设置HeaderView LayoutParams
             *
             * @param layoutParams LayoutParams
             */
    fun setHeaderLayoutParams(layoutParams: LinearLayout.LayoutParams) {
        if (mHeaderContainer != null) {
            mHeaderContainer!!.layoutParams = layoutParams
            mHeaderHeight = layoutParams.height
            isCustomHeaderHeight = true
        }
    }

    override fun onLayout(paramBoolean: Boolean, paramInt1: Int, paramInt2: Int,
                          paramInt3: Int, paramInt4: Int) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4)
        if (mHeaderHeight == 0 && mZoomView != null) {
            mHeaderHeight = mHeaderContainer!!.height
        }
    }

    inner class ScalingRunnable : Runnable {
        private var mDuration: Long = 0
        var isFinished = true
        private var mScale: Float = 0.toFloat()
        private var mStartTime: Long = 0

        fun abortAnimation() {
            isFinished = true
        }

        override fun run() {
            if (mZoomView != null) {
                val f2: Float
                val localLayoutParams: ViewGroup.LayoutParams
                if (!isFinished && mScale > 1.0) {
                    val f1 = (SystemClock.currentThreadTimeMillis().toFloat() - mStartTime.toFloat()) / mDuration.toFloat()
                    f2 = mScale - (mScale - 1.0f) * PullToZoomScrollViewEx.sInterpolator.getInterpolation(f1)
                    localLayoutParams = mHeaderContainer!!.layoutParams
                    if (f2 > 1.0f) {
                        localLayoutParams.height = (f2 * mHeaderHeight).toInt()
                        mHeaderContainer!!.layoutParams = localLayoutParams
                        if (isCustomHeaderHeight) {
                            val zoomLayoutParams: ViewGroup.LayoutParams? = mZoomView?.layoutParams
                            zoomLayoutParams?.let {
                                it.height = (f2 * mHeaderHeight).toInt()
                                mZoomView?.layoutParams = it
                            }
                        }
                        post(this)
                        return
                    }
                    isFinished = true
                }
            }
        }

        fun startAnimation(paramLong: Long) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis()
                mDuration = paramLong
                mScale = mHeaderContainer!!.bottom.toFloat() / mHeaderHeight
                isFinished = false
                post(this)
            }
        }
    }

    private inner class InternalScrollView
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ScrollView(context, attrs) {
        private var onScrollViewChangedListener: OnScrollViewChangedListener? = null

        fun setOnScrollViewChangedListener(onScrollViewChangedListener: OnScrollViewChangedListener) {
            this.onScrollViewChangedListener = onScrollViewChangedListener
        }

        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            super.onScrollChanged(l, t, oldl, oldt)
            if (onScrollViewChangedListener != null) {
                onScrollViewChangedListener!!.onInternalScrollChanged(l, t, oldl, oldt)
            }
        }
    }

    private interface OnScrollViewChangedListener {
        fun onInternalScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int)
    }

    companion object {
        private val TAG = PullToZoomScrollViewEx::class.java.simpleName

        private val sInterpolator = Interpolator { paramAnonymousFloat ->
            val f = paramAnonymousFloat - 1.0f
            1.0f + f * (f * (f * (f * f)))
        }
    }
}