import android.support.v4.view.ViewPager

/**
 * A class to synchronize scrolling between two viewpagers.
 */
class SyncPager(
        private val primary: ViewPager,
        private val secondary: ViewPager
) : ViewPager.OnPageChangeListener {

    companion object {

        /**
         * Synchronize two viewpagers.
         */
        fun synchronizePagers(pager1: ViewPager, pager2: ViewPager) {
            SyncPager(pager1, pager2)
            SyncPager(pager2, pager1)
        }
    }

    init {

        // Add this as a page change listener to the primary.
        primary.addOnPageChangeListener(this)
    }

    // Vars to hold values from one call to 'onPageScrolled' to the next.
    private var lastPagePosition = 0
    private var lastTotalPosition = 0f
    private var lastSecondaryPosition = 0

    override fun onPageScrollStateChanged(state: Int) {
        if (primary.isFakeDragging) return
        when (state) {

            // If primary starts dragging, then start fake drag on secondary.
            ViewPager.SCROLL_STATE_DRAGGING -> secondary.beginFakeDrag()

            // If primary stops dragging...
            ViewPager.SCROLL_STATE_SETTLING,
            ViewPager.SCROLL_STATE_IDLE -> {

                // Set page positions to settled values.
                lastPagePosition = primary.currentItem
                lastSecondaryPosition = (lastPagePosition * getAdjustedPageWidth(secondary))
                lastTotalPosition = lastPagePosition.toFloat()

                // If secondary was fake dragging...
                if (secondary.isFakeDragging) {

                    // End drag and set current item.
                    secondary.endFakeDrag()
                    secondary.setCurrentItem(primary.currentItem, true)
                }
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (primary.isFakeDragging) return
        if (!secondary.isFakeDragging) return

        // Get primary position from beginning of list.
        val absolutePosition = position + positionOffset

        // If position is greater than last time, we are scrolling to the left.
        val scrollingLeft = (absolutePosition > lastTotalPosition)
        lastTotalPosition = absolutePosition

        // If page has changed...
        if (lastPagePosition != position) {

            // If scrolling right, then adjust secondary item.
            var secondaryItem = position
            if (!scrollingLeft) {
                secondaryItem += 1
            }

            // Set secondary current item.
            secondary.setCurrentItem(secondaryItem, false)

            // Update last page position.
            lastPagePosition = position
        }

        // Secondary position in pixels should be primary position * secondary page width.
        val desiredSecondaryPosition = absolutePosition * getAdjustedPageWidth(secondary)

        // Calculate difference from last secondary position.
        val deltaDrag = lastSecondaryPosition - (desiredSecondaryPosition.toInt())

        // Fake drag secondary.
        secondary.fakeDragBy(deltaDrag.toFloat())

        // Update last secondary position.
        lastSecondaryPosition -= deltaDrag
    }

    override fun onPageSelected(position: Int) {
        lastPagePosition = position
    }

    /**
     * Values for positionOffsetPixels are based on how far a page moves from
     * one position to the next. This is the pager width minus any page margin.
     */
    private fun getAdjustedPageWidth(pager: ViewPager): Int {
        val pagerWidth = pager.width
        val margin = pager.pageMargin
        return pagerWidth + margin
    }
}
