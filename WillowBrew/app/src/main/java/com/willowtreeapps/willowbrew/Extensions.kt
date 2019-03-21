package com.willowtreeapps.willowbrew

import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout

/**
 * Prevent a swipe to refresh from swiping if the viewpager is being scrolled.
 */


fun SwipeRefreshLayout.blockWhenScrolling(viewPager: ViewPager?) {

    viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            if (!isRefreshing) {
                isEnabled = (state == ViewPager.SCROLL_STATE_IDLE)
            }
        }

        override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
        }
    })
}

