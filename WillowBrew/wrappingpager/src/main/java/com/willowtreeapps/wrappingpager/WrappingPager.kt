package com.willowtreeapps.willowbrew

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
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

                    hms = View.MeasureSpec.makeMeasureSpec(getHeightOfTallestChild(widthMeasureSpec), View.MeasureSpec.EXACTLY)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.d("WRAP", "Page: $currentPage, Measure: $heightMeasureSpec, $hms")
        super.onMeasure(widthMeasureSpec, hms)
    }

    fun measurePage(position: Int) {
        currentPage = position
        requestLayout()
    }

    private fun getHeightOfTallestChild(widthMeasureSpec: Int): Int {
        var height = 0
        for(x in 0 until this.childCount) {
            val child = getChildAt(x)
            if (child != null) {
                child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                if (child.measuredHeight > height) {
                    height = child.measuredHeight
                }
            }
        }
        return height
    }

}
