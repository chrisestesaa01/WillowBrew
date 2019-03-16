package com.willowtreeapps.willowbrew

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.R
import android.content.Context
import android.util.Log
import android.view.View




class BeveragePagerAdapter(
        private val context: Context,
        fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager), ViewPager.PageTransformer {

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

        return BeveragePageFragement.newFragment(context, p0, scale)
    }

    override fun transformPage(page: View, position: Float) {
        val a = page.width
        val b = page.parent
        val c = (b as ViewPager).width

        val offset = ((c.toFloat() - a) / 2) / a
        //Log.d("POS", "$position ")



        var pos = position
//        if (pos < offset) {
//            pos = 1 - (offset - pos)
//        } else {
            pos = pos - offset
//        }

        val myLinearLayout = page.findViewById(com.willowtreeapps.willowbrew.R.id.item_include) as CarouselLayout?
        var scale: Float = BIG_SCALE
        if (pos > 0) {
            scale = scale - pos * BeveragePagerAdapter.DIFF_SCALE
        } else {
            scale = scale + pos * BeveragePagerAdapter.DIFF_SCALE
        }
        var alpha: Float = 1f
        alpha = if (pos > 0) {
            alpha - pos * 0.9f
        } else {
            alpha + pos * 0.9f
        }
        myLinearLayout?.setScaleBoth(scale)
        myLinearLayout?.alpha = alpha
    }



    fun setItems(items: List<BeveragePageViewModel.BeverageModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

}
