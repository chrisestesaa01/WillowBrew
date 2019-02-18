package com.willowtreeapps.willowbrew

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.LinearLayout
import com.willowtreeapps.willowbrew.BeveragePagerAdapter.Companion.BIG_SCALE


class CarouselLayout : LinearLayout {
    private var mScale = BIG_SCALE


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    fun setScaleBoth(scale: Float) {
        this.mScale = scale
        this.invalidate()    // If you want to see the mScale every time you set
        // mScale you need to have this line here,
        // invalidate() function will call onDraw(Canvas)
        // to redraw the view for you
    }

    override fun onDraw(canvas: Canvas) {
        // The main mechanism to display mScale animation, you can customize it
        // as your needs
        val w = this.width
        val h = this.height
        canvas.scale(mScale, mScale, w / 2f, 0f)

        super.onDraw(canvas)
    }
}