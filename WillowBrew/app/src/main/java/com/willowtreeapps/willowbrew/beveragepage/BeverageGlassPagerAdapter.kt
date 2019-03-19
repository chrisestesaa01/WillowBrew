package com.willowtreeapps.willowbrew.beveragepage

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.content.Context
import android.view.View
import com.willowtreeapps.willowbrew.CarouselLayout
import com.willowtreeapps.willowbrew.R
import java.lang.Math.abs

class BeverageGlassPagerAdapter(
        private val context: Context,
        fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager), ViewPager.PageTransformer {

    companion object {
        const val BIG_SCALE = 1.0f
        const val SMALL_SCALE = 0.7f
        const val DIFF_SCALE = BIG_SCALE - SMALL_SCALE
        const val FIRST_PAGE = 0
        const val ALPHA_VALUE = 0.4f
    }

    private var scale: Float = 0.0f

    private var count = 0

    fun setCount(count: Int) {
        this.count = count
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return count
    }

    override fun getItem(position: Int): Fragment {

        scale = if (position == FIRST_PAGE) {
            BIG_SCALE
        } else {
            SMALL_SCALE
        }

        return BeverageGlassFragement.newFragment(context, position, scale)
    }

    override fun transformPage(page: View, position: Float) {

        // Get page position offset from center.
        val a = page.width
        val b = page.parent
        val c = if (b is ViewPager) {
            b.width + b.pageMargin
        } else {
            a
        }
        val d = c.toFloat() / a
        val adjustedPosition = (1 - (abs(position) / d))

        // Calculate scale and alpha from position.
        val scale: Float = SMALL_SCALE + (DIFF_SCALE * adjustedPosition)
        val alpha = ALPHA_VALUE + ((1f - ALPHA_VALUE) * adjustedPosition)

        // Set scale and alpha value for page.
        val glassLayout = page.findViewById(R.id.item_include) as? CarouselLayout?
        glassLayout?.setScaleBoth(scale)
        glassLayout?.alpha = alpha
    }
}
