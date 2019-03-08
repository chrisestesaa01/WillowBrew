package com.willowtreeapps.willowbrew

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class SyncPager : ViewPager {

    private var pagerToSync: SyncPager? = null

    private var forSuper = false

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!forSuper) {
            pagerToSync?.forSuper = true
            pagerToSync?.onInterceptTouchEvent(ev)
            pagerToSync?.forSuper = false
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (!forSuper) {
            pagerToSync?.forSuper = true

            val ev2 = MotionEvent.obtain(ev)
            val w1 = this.width + this.pageMargin
            val w2 = pagerToSync?.let { it.width + it.pageMargin } ?: 0
            val foo: Float = (pagerToSync?.x ?: 0f)

            ev2.setLocation( foo + ev2.x * w2 / w1, ev2.y)
            pagerToSync?.onTouchEvent(ev2)
            ev2.recycle()
            pagerToSync?.forSuper = false
        }
        return super.onTouchEvent(ev)
    }

    fun setPagerToSync(pager: SyncPager) {
        this.pagerToSync = pager
    }

    fun setForSuper(forSuper: Boolean) {
        this.forSuper = forSuper
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        if (!forSuper) {
            pagerToSync?.forSuper = true
            pagerToSync?.setCurrentItem(item, smoothScroll)
            pagerToSync?.forSuper = false
        }
        super.setCurrentItem(item, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        if (!forSuper) {
            pagerToSync?.forSuper = true
            pagerToSync?.setCurrentItem(item)
            pagerToSync?.forSuper = false
        }
        super.setCurrentItem(item)

    }
}