package com.willowtreeapps.willowbrew

import android.content.Context
import android.graphics.Canvas
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

/**
 * A ConstraintLayout that can be scaled.
 */
class CarouselLayout : ConstraintLayout {

    private var scale = 0f

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    fun setScaleBoth(scale: Float) {

        // Set scale and redraw.
        this.scale = scale
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas) {

        // Scale canvas by current value and draw.
        val w = this.width
        canvas.scale(scale, scale, w / 2f, 0f)
        super.onDraw(canvas)
    }
}
