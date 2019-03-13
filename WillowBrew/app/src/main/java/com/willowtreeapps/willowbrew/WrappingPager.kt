package com.willowtreeapps.willowbrew

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

class WrappingPager : ViewPager {

    init {
        this.addOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                measurePage(p0)
            }
        })
    }

    private var currentPage = 0

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var hms = heightMeasureSpec
        try {
            val wrapHeight = View.MeasureSpec.getMode(hms) == View.MeasureSpec.AT_MOST
            if (wrapHeight) {
                val child = getChildAt(currentPage)
                if (child != null) {
                    child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                    val h = child.measuredHeight

                    hms = View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onMeasure(widthMeasureSpec, hms)
    }

    fun measurePage(position: Int) {
        currentPage = position
        requestLayout()
    }
}
