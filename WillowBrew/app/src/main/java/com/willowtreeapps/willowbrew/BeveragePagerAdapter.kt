package com.willowtreeapps.willowbrew

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.R
import android.content.Context
import android.view.View




class BeveragePagerAdapter(
        private val context: Context,
        fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager), ViewPager.PageTransformer {

    private val items: MutableList<BeveragePageModel> = mutableListOf()

    companion object {
        const val BIG_SCALE = 1.0f
        const val SMALL_SCALE = 0.6f
        val DIFF_SCALE = BIG_SCALE - SMALL_SCALE
        const val FIRST_PAGE = 0

    }
    private var scale: Float = 0.0f

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Fragment {

        scale = if (p0 == FIRST_PAGE) {
            BIG_SCALE
        } else {
            SMALL_SCALE
        }

        return BeveragePageFragement.newFragment(context, p0, scale)
    }

    override fun transformPage(page: View, position: Float) {
        val myLinearLayout = page.findViewById(com.willowtreeapps.willowbrew.R.id.item_include) as CarouselLayout?
        var scale: Float = BIG_SCALE
        if (position > 0) {
            scale = scale - position * BeveragePagerAdapter.DIFF_SCALE
        } else {
            scale = scale + position * BeveragePagerAdapter.DIFF_SCALE
        }
        var alpha: Float = 1f
        alpha = if (position > 0) {
            alpha - position * 0.9f
        } else {
            alpha + position * 0.9f
        }
        myLinearLayout?.setScaleBoth(scale)
        myLinearLayout?.alpha = alpha
    }

}
