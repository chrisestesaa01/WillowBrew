package com.willowtreeapps.willowbrew.beveragepage

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.content.Context

class BeverageCardPagerAdapter(
        private val context: Context,
        fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    private var count = 0

    fun setCount(count: Int) {
        this.count = count
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return count
    }

    override fun getItem(position: Int): Fragment {
        return BeverageCardFragement.newFragment(context, position)
    }
}
