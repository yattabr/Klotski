package br.com.wobbu.klotski.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller

class MyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    android.support.v7.widget.AppCompatButton(context, attrs, defStyleAttr) {
    internal var scroller: Scroller? = null
    internal var direction = -1

    init {
        scroller = Scroller(context)
    }

    override fun computeScroll() {
        if (scroller != null) {
            if (scroller!!.computeScrollOffset()) {
                (parent as View).scrollTo(
                    scroller!!.currX, scroller!!.currY
                )

                invalidate()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scroller!!.startScroll(
                    x.toInt(), y.toInt(), x.toInt() * direction,
                    y.toInt() * direction
                )
                direction *= -1
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }
}