package com.willowtreeapps.wrappingpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

class WrappingPager : ViewPager {

    // Update once, or update on each page change.
    private val updateOnPageChange: Boolean

    // Whether to wrap height, width, or both.
    private val wrappingStyle: Int

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet) {

        // Initialize update and wrap type.
        if (attributeSet == null) {
            updateOnPageChange = false
            wrappingStyle = 0

        } else {

            val attrs = context.theme.obtainStyledAttributes(
                    attributeSet,
                    R.styleable.WrappingPager,
                    0,
                    0
            )

            try {
                updateOnPageChange = attrs.getBoolean(R.styleable.WrappingPager_updateOnPageChange, false)
                wrappingStyle = attrs.getInteger(R.styleable.WrappingPager_wrappingStyle, 0)
            } finally {
                attrs.recycle()
            }
        }

        // Add a listener to update the layout on page change.
        this.addOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {

                // Request a layout to trigger a measure.
                requestLayout()

                // If not updating on page changes, then remove this listener.
                if (!updateOnPageChange) {
                    this@WrappingPager.removeOnPageChangeListener(this)
                }
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // Get local vars for width and height specs.
        var hms = heightMeasureSpec
        var wms = widthMeasureSpec

        try {

            // If we are wrapping all or wrapping height...
            val wrapHeight = View.MeasureSpec.getMode(hms) == View.MeasureSpec.AT_MOST
            if ((wrappingStyle == 2 || wrappingStyle == 0) && wrapHeight) {

                // Then set height spec from the tallest child view.
                hms = View.MeasureSpec.makeMeasureSpec(
                        getHeightOfTallestChild(widthMeasureSpec),
                        View.MeasureSpec.EXACTLY
                )
            }

            // If we are wrapping all or wrapping width...
            val wrapWidth = View.MeasureSpec.getMode(wms) == View.MeasureSpec.AT_MOST
            if ((wrappingStyle == 2 || wrappingStyle == 1) && wrapWidth) {

                // Then set width spec from widest child.
                wms = View.MeasureSpec.makeMeasureSpec(
                        getWidthOfWidestChild(heightMeasureSpec),
                        View.MeasureSpec.EXACTLY
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Call onMeasure with updated specs.
        super.onMeasure(wms, hms)
    }

    /**
     * Get height of tallest child.
     */
    private fun getHeightOfTallestChild(widthMeasureSpec: Int): Int {

        // Init result.
        var height = 0

        // For all children...
        for(x in 0 until this.childCount) {

            // Get child.
            val child = getChildAt(x)
            if (child != null) {

                // Measure child.
                child.measure(
                        widthMeasureSpec,
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                // Update result.
                if (child.measuredHeight > height) {
                    height = child.measuredHeight
                }
            }
        }
        return height
    }

    /**
     * Get width of widest child.
     */
    private fun getWidthOfWidestChild(heightMeasureSpec: Int): Int {

        // Init result.
        var width = 0

        // For all children...
        for(x in 0 until this.childCount) {

            // Get child.
            val child = getChildAt(x)
            if (child != null) {

                // Measure child.
                child.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        heightMeasureSpec
                )

                // Update result.
                if (child.measuredWidth > width) {
                    width = child.measuredWidth
                }
            }
        }
        return width
    }
}
