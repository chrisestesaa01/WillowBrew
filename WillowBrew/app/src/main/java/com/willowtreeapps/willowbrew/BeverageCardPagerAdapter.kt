package com.willowtreeapps.willowbrew

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.R
import android.content.Context
import android.view.View




class BeverageCardPagerAdapter(
        private val context: Context,
        fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    private val items: MutableList<BeveragePageViewModel.BeverageModel> = mutableListOf()

    companion object {
        const val BIG_SCALE = 1.0f
        const val SMALL_SCALE = 0.6f
        val DIFF_SCALE = BIG_SCALE - SMALL_SCALE
        const val FIRST_PAGE = 0

    }
    private var scale: Float = 0.0f

    override fun getCount(): Int {
        return 10
    }

    override fun getItem(p0: Int): Fragment {

        scale = if (p0 == FIRST_PAGE) {
            BIG_SCALE
        } else {
            SMALL_SCALE
        }

        return BeverageCardFragement.newFragment(context, p0)
    }


}
