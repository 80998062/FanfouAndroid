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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView

/**
 * Created by florentchampigny on 02/06/2017.
 */

class ViewTooltip private constructor(myContext: MyContext, private val view: View) {
    private val tooltip_view: TooltipView

    init {
        this.tooltip_view = TooltipView(myContext.getContext())
        val scrollParent = findScrollParent(view)
        scrollParent?.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                    tooltip_view.translationY = tooltip_view.translationY - (scrollY - oldScrollY)
                })
    }

    private constructor(view: View) : this(MyContext(activity = getActivityContext(view.context)), view) {}

    private fun findScrollParent(view: View): NestedScrollView? {
        return if (view.parent == null || view.parent !is View) {
            null
        } else if (view.parent is NestedScrollView) {
            view.parent as NestedScrollView
        } else {
            findScrollParent(view.parent as View)
        }
    }

    fun position(position: Position): ViewTooltip {
        this.tooltip_view.setPosition(position)
        return this
    }

    fun withShadow(withShadow: Boolean): ViewTooltip {
        this.tooltip_view.setWithShadow(withShadow)
        return this
    }

    fun customView(customView: View): ViewTooltip {
        this.tooltip_view.setCustomView(customView)
        return this
    }

    fun customView(viewId: Int): ViewTooltip {
        this.tooltip_view.setCustomView((view.context as Activity).findViewById(viewId))
        return this
    }

    fun arrowWidth(arrowWidth: Int): ViewTooltip {
        this.tooltip_view.arrowWidth = arrowWidth
        return this
    }

    fun arrowHeight(arrowHeight: Int): ViewTooltip {
        this.tooltip_view.arrowHeight = arrowHeight
        return this
    }

    fun align(align: ALIGN): ViewTooltip {
        this.tooltip_view.setAlign(align)
        return this
    }

    fun show(): TooltipView {
        val activityContext = tooltip_view.context
        if (activityContext != null && activityContext is Activity) {
            val decorView = activityContext.window.decorView as ViewGroup
            view.postDelayed({
                val rect = Rect()
                view.getGlobalVisibleRect(rect)

                val location = IntArray(2)
                view.getLocationOnScreen(location)
                rect.left = location[0]
                //rect.left = location[0];

                decorView.addView(tooltip_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                tooltip_view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {

                        tooltip_view.setup(rect, decorView.width)

                        tooltip_view.viewTreeObserver.removeOnPreDrawListener(this)

                        return false
                    }
                })
            }, 100)
        }
        return tooltip_view
    }

    fun close() {
        tooltip_view.close()
    }

    fun duration(duration: Long): ViewTooltip {
        this.tooltip_view.setDuration(duration)
        return this
    }

    fun color(color: Int): ViewTooltip {
        this.tooltip_view.setColor(color)
        return this
    }

    fun onDisplay(listener: ListenerDisplay): ViewTooltip {
        this.tooltip_view.setListenerDisplay(listener)
        return this
    }

    fun onHide(listener: ListenerHide): ViewTooltip {
        this.tooltip_view.setListenerHide(listener)
        return this
    }

    fun padding(left: Int, top: Int, right: Int, bottom: Int): ViewTooltip {
        this.tooltip_view.setPadding(left, top, right, bottom)
        return this
    }

    fun animation(tooltipAnimation: TooltipAnimation): ViewTooltip {
        this.tooltip_view.setTooltipAnimation(tooltipAnimation)
        return this
    }

    fun text(text: String): ViewTooltip {
        this.tooltip_view.setText(text)
        return this
    }

    fun corner(corner: Int): ViewTooltip {
        this.tooltip_view.setCorner(corner)
        return this
    }

    fun textColor(textColor: Int): ViewTooltip {
        this.tooltip_view.setTextColor(textColor)
        return this
    }

    fun textTypeFace(typeface: Typeface): ViewTooltip {
        this.tooltip_view.setTextTypeFace(typeface)
        return this
    }

    fun textSize(unit: Int, textSize: Float): ViewTooltip {
        this.tooltip_view.setTextSize(unit, textSize)
        return this
    }

    fun setTextGravity(textGravity: Int): ViewTooltip {
        this.tooltip_view.setTextGravity(textGravity)
        return this
    }

    fun clickToHide(clickToHide: Boolean): ViewTooltip {
        this.tooltip_view.setClickToHide(clickToHide)
        return this
    }

    fun autoHide(autoHide: Boolean, duration: Long): ViewTooltip {
        this.tooltip_view.setAutoHide(autoHide)
        this.tooltip_view.setDuration(duration)
        return this
    }

    fun distanceWithView(distance: Int): ViewTooltip {
        this.tooltip_view.setDistanceWithView(distance)
        return this
    }

    enum class Position {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    enum class ALIGN {
        START,
        CENTER,
        END
    }

    interface TooltipAnimation {
        fun animateEnter(view: View, animatorListener: Animator.AnimatorListener)

        fun animateExit(view: View, animatorListener: Animator.AnimatorListener)
    }

    interface ListenerDisplay {
        fun onDisplay(view: View)
    }

    interface ListenerHide {
        fun onHide(view: View)
    }

    class FadeTooltipAnimation constructor(var fadeDuration: Long = 400) : TooltipAnimation {

        override fun animateEnter(view: View, animatorListener: Animator.AnimatorListener) {
            view.alpha = 0f
            view.animate().alpha(1f).setDuration(fadeDuration).setListener(animatorListener)
        }

        override fun animateExit(view: View, animatorListener: Animator.AnimatorListener) {
            view.animate().alpha(0f).setDuration(fadeDuration).setListener(animatorListener)
        }
    }

    class TooltipView(context: Context?) : FrameLayout(context) {
        var arrowHeight = 15
            set(arrowHeight) {
                field = arrowHeight
                postInvalidate()
            }
        var arrowWidth = 15
            set(arrowWidth) {
                field = arrowWidth
                postInvalidate()
            }
        private var childView: View
        private var color = Color.parseColor("#1F7C82")
        private var bubblePath: Path? = null
        private val bubblePaint: Paint
        private var position = Position.BOTTOM
        private var align = ALIGN.CENTER
        private var clickToHide: Boolean = false
        private var autoHide = true
        private var duration: Long = 4000

        private var listenerDisplay: ListenerDisplay? = null

        private var listenerHide: ListenerHide? = null

        private var tooltipAnimation: TooltipAnimation = FadeTooltipAnimation()

        private var corner = 30

        init {
            setPadding(30, 20, 30, 30)
        }


        private var shadowPadding = 4
        private var shadowWidth = 8

        private var viewRect: Rect? = null
        private var distanceWithView = 0

        init {
            setWillNotDraw(false)

            this.childView = TextView(context)
            (childView as TextView).setTextColor(Color.WHITE)
            addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            childView.setPadding(0, 0, 0, 0)

            bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            bubblePaint.color = color
            bubblePaint.style = Paint.Style.FILL


            setLayerType(View.LAYER_TYPE_SOFTWARE, bubblePaint)

            setWithShadow(true)

        }

        fun setCustomView(customView: View) {
            this.removeView(childView)
            this.childView = customView
            addView(childView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        fun setColor(color: Int) {
            this.color = color
            bubblePaint.color = color
            postInvalidate()
        }

        fun setPosition(position: Position) {
            this.position = position
            when (position) {
                ViewTooltip.Position.TOP -> setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + this.arrowHeight)
                ViewTooltip.Position.BOTTOM -> setPadding(paddingLeft, paddingTop + this.arrowHeight, paddingRight, paddingBottom)
                ViewTooltip.Position.LEFT -> setPadding(paddingLeft, paddingTop, paddingRight + this.arrowHeight, paddingBottom)
                ViewTooltip.Position.RIGHT -> setPadding(paddingLeft + this.arrowHeight, paddingTop, paddingRight, paddingBottom)
            }
            postInvalidate()
        }

        fun setAlign(align: ALIGN) {
            this.align = align
            postInvalidate()
        }

        fun setText(text: String) {
            if (childView is TextView) {
                (this.childView as TextView).text = Html.fromHtml(text)
            }
            postInvalidate()
        }

        fun setTextColor(textColor: Int) {
            if (childView is TextView) {
                (this.childView as TextView).setTextColor(textColor)
            }
            postInvalidate()
        }

        fun setTextTypeFace(textTypeFace: Typeface) {
            if (childView is TextView) {
                (this.childView as TextView).typeface = textTypeFace
            }
            postInvalidate()
        }

        fun setTextSize(unit: Int, size: Float) {
            if (childView is TextView) {
                (this.childView as TextView).setTextSize(unit, size)
            }
            postInvalidate()
        }

        fun setTextGravity(textGravity: Int) {
            if (childView is TextView) {
                (this.childView as TextView).gravity = textGravity
            }
            postInvalidate()
        }

        fun setClickToHide(clickToHide: Boolean) {
            this.clickToHide = clickToHide
        }

        fun setCorner(corner: Int) {
            this.corner = corner
        }

        override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(width, height, oldw, oldh)

            bubblePath = drawBubble(RectF(shadowPadding.toFloat(), shadowPadding.toFloat(), (width - shadowPadding * 2).toFloat(), (height - shadowPadding * 2).toFloat()), corner.toFloat(), corner.toFloat(), corner.toFloat(), corner.toFloat())
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            if (bubblePath != null) {
                canvas.drawPath(bubblePath!!, bubblePaint)
            }
        }

        fun setListenerDisplay(listener: ListenerDisplay) {
            this.listenerDisplay = listener
        }

        fun setListenerHide(listener: ListenerHide) {
            this.listenerHide = listener
        }

        fun setTooltipAnimation(tooltipAnimation: TooltipAnimation) {
            this.tooltipAnimation = tooltipAnimation
        }

        protected fun startEnterAnimation() {
            tooltipAnimation.animateEnter(this, object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (listenerDisplay != null) {
                        listenerDisplay!!.onDisplay(this@TooltipView)
                    }
                }
            })
        }

        protected fun startExitAnimation(animatorListener: Animator.AnimatorListener) {
            tooltipAnimation.animateExit(this, object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animatorListener.onAnimationEnd(animation)
                    if (listenerHide != null) {
                        listenerHide!!.onHide(this@TooltipView)
                    }
                }
            })
        }

        protected fun handleAutoRemove() {
            if (clickToHide) {
                setOnClickListener {
                    if (clickToHide) {
                        remove()
                    }
                }
            }

            if (autoHide) {
                postDelayed({ remove() }, duration)
            }
        }

        fun remove() {
            startExitAnimation(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    removeNow()
                }
            })
        }

        fun setDuration(duration: Long) {
            this.duration = duration
        }

        fun setAutoHide(autoHide: Boolean) {
            this.autoHide = autoHide
        }

        fun setupPosition(rect: Rect) {

            val x: Int
            val y: Int

            if (position == Position.LEFT || position == Position.RIGHT) {
                if (position == Position.LEFT) {
                    x = rect.left - width - distanceWithView
                } else {
                    x = rect.right + distanceWithView
                }
                y = rect.top + getAlignOffset(height, rect.height())
            } else {
                if (position == Position.BOTTOM) {
                    y = rect.bottom + distanceWithView
                } else { // top
                    y = rect.top - height - distanceWithView
                }
                x = rect.left + getAlignOffset(width, rect.width())
            }

            translationX = x.toFloat()
            translationY = y.toFloat()
        }

        private fun getAlignOffset(myLength: Int, hisLength: Int): Int {
            return when (align) {
                ViewTooltip.ALIGN.END -> hisLength - myLength
                ViewTooltip.ALIGN.CENTER -> (hisLength - myLength) / 2
                else -> 0
            }
        }

        private fun drawBubble(myRect: RectF, topLeftDiameter: Float, topRightDiameter: Float, bottomRightDiameter: Float, bottomLeftDiameter: Float): Path {
            val path = Path()

            if (viewRect == null)
                return path

            val spacingLeft = (if (this.position == Position.RIGHT) this.arrowHeight else 0).toFloat()
            val spacingTop = (if (this.position == Position.BOTTOM) this.arrowHeight else 0).toFloat()
            val spacingRight = (if (this.position == Position.LEFT) this.arrowHeight else 0).toFloat()
            val spacingBottom = (if (this.position == Position.TOP) this.arrowHeight else 0).toFloat()

            val left = spacingLeft + myRect.left
            val top = spacingTop + myRect.top
            val right = myRect.right - spacingRight
            val bottom = myRect.bottom - spacingBottom
            val centerX = viewRect!!.centerX() - x

            path.moveTo(left + topLeftDiameter / 2f, top)
            //LEFT, TOP

            if (position == Position.BOTTOM) {
                path.lineTo(centerX - this.arrowWidth, top)
                path.lineTo(centerX, myRect.top)
                path.lineTo(centerX + this.arrowWidth, top)
            }
            path.lineTo(right - topRightDiameter / 2f, top)

            path.quadTo(right, top, right, top + topRightDiameter / 2)
            //RIGHT, TOP

            if (position == Position.LEFT) {
                path.lineTo(right, bottom / 2f - this.arrowWidth)
                path.lineTo(myRect.right, bottom / 2f)
                path.lineTo(right, bottom / 2f + this.arrowWidth)
            }
            path.lineTo(right, bottom - bottomRightDiameter / 2)

            path.quadTo(right, bottom, right - bottomRightDiameter / 2, bottom)
            //RIGHT, BOTTOM

            if (position == Position.TOP) {
                path.lineTo(centerX + this.arrowWidth, bottom)
                path.lineTo(centerX, myRect.bottom)
                path.lineTo(centerX - this.arrowWidth, bottom)
            }
            path.lineTo(left + bottomLeftDiameter / 2, bottom)

            path.quadTo(left, bottom, left, bottom - bottomLeftDiameter / 2)
            //LEFT, BOTTOM

            if (position == Position.RIGHT) {
                path.lineTo(left, bottom / 2f + this.arrowWidth)
                path.lineTo(myRect.left, bottom / 2f)
                path.lineTo(left, bottom / 2f - this.arrowWidth)
            }
            path.lineTo(left, top + topLeftDiameter / 2)

            path.quadTo(left, top, left + topLeftDiameter / 2, top)

            path.close()

            return path
        }

        fun adjustSize(rect: Rect, screenWidth: Int): Boolean {

            val r = Rect()
            getGlobalVisibleRect(r)

            var changed = false
            val layoutParams = layoutParams
            if (position == Position.LEFT && width > rect.left) {
                layoutParams.width = rect.left - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
                changed = true
            } else if (position == Position.RIGHT && rect.right + width > screenWidth) {
                layoutParams.width = screenWidth - rect.right - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
                changed = true
            } else if (position == Position.TOP || position == Position.BOTTOM) {
                var adjustedLeft = rect.left
                var adjustedRight = rect.right

                if (rect.centerX() + width / 2f > screenWidth) {
                    val diff = rect.centerX() + width / 2f - screenWidth

                    adjustedLeft -= diff.toInt()
                    adjustedRight -= diff.toInt()

                    setAlign(ALIGN.CENTER)
                    changed = true
                } else if (rect.centerX() - width / 2f < 0) {
                    val diff = -(rect.centerX() - width / 2f)

                    adjustedLeft += diff.toInt()
                    adjustedRight += diff.toInt()

                    setAlign(ALIGN.CENTER)
                    changed = true
                }

                if (adjustedLeft < 0) {
                    adjustedLeft = 0
                }

                if (adjustedRight > screenWidth) {
                    adjustedRight = screenWidth
                }

                rect.left = adjustedLeft
                rect.right = adjustedRight
            }

            setLayoutParams(layoutParams)
            postInvalidate()
            return changed
        }

        private fun onSetup(myRect: Rect) {
            setupPosition(myRect)

            bubblePath = drawBubble(RectF(shadowPadding.toFloat(), shadowPadding.toFloat(), width - shadowPadding * 2f, height - shadowPadding * 2f), corner.toFloat(), corner.toFloat(), corner.toFloat(), corner.toFloat())
            startEnterAnimation()

            handleAutoRemove()
        }

        fun setup(viewRect: Rect, screenWidth: Int) {
            this.viewRect = Rect(viewRect)
            val myRect = Rect(viewRect)

            val changed = adjustSize(myRect, screenWidth)
            if (!changed) {
                onSetup(myRect)
            } else {
                viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        onSetup(myRect)
                        viewTreeObserver.removeOnPreDrawListener(this)
                        return false
                    }
                })
            }
        }

        fun close() {
            remove()
        }

        fun removeNow() {
            if (parent != null) {
                val parent = parent as ViewGroup
                parent.removeView(this@TooltipView)
            }
        }

        fun closeNow() {
            removeNow()
        }

        fun setWithShadow(withShadow: Boolean) {
            if (withShadow) {
                bubblePaint.setShadowLayer(shadowWidth.toFloat(), 0f, 0f, Color.parseColor("#aaaaaa"))
            } else {
                bubblePaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            }
        }

        fun setDistanceWithView(distanceWithView: Int) {
            this.distanceWithView = distanceWithView
        }

        companion object {

            private val MARGIN_SCREEN_BORDER_TOOLTIP = 30
        }
    }

    class MyContext constructor(private val fragment: Fragment? = null,
                                private val context: Context? = null,
                                private val activity: Activity? = null) {

        val window: Window?
            get() = if (activity != null) {
                activity.window
            } else {
                if (fragment is DialogFragment) {
                    fragment.dialog.window
                } else fragment?.activity?.window
            }

        fun getContext(): Context? {
            return activity ?: fragment?.context as Context?
        }

        fun getActivity(): Activity? {
            return activity ?: fragment?.activity
        }
    }

    companion object {

        fun on(view: View): ViewTooltip {
            return ViewTooltip(MyContext(activity = getActivityContext(context = view.context)), view)
        }

        fun on(fragment: Fragment, view: View): ViewTooltip {
            return ViewTooltip(MyContext(fragment), view)
        }

        fun on(activity: Activity, view: View): ViewTooltip {
            return ViewTooltip(MyContext(activity = getActivityContext(activity)), view)
        }

        private fun getActivityContext(context: Context): Activity? {
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context.baseContext
            }
            return null
        }
    }
}