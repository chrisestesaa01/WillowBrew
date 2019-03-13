import android.support.v4.view.ViewPager
import android.util.Log
import kotlin.math.abs

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

    // Vars to hold values from one call to 'onPageScroled' to the next.
    var lastPagePosition = 0
    var lastScrollOffset = 0
    var lastAbsolutePosition = 0

    override fun onPageScrollStateChanged(state: Int) {
        if (primary.isFakeDragging) return
        when (state) {

            // If primary starts dragging, then start fake drag on secondary.
            ViewPager.SCROLL_STATE_DRAGGING -> secondary.beginFakeDrag()

            // If primary stops dragging...
            ViewPager.SCROLL_STATE_SETTLING,
            ViewPager.SCROLL_STATE_IDLE -> {

                // Reset scroll offset and set last page.
                lastScrollOffset = 0
                lastPagePosition = primary.currentItem

                // Set absolute position to page position * page width.
                lastAbsolutePosition = (lastPagePosition * getPageWidth(primary))

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

        // Get page width.
        val width = getPageWidth(primary)

        // Get total pixel offset from beginning of list.
        val absolutePosition = positionOffsetPixels + (position * width)

        // If total position is greater than last time, we are scrolling to the left.
        val scrollingLeft = (absolutePosition > lastAbsolutePosition)

        // Update last absolute position.
        lastAbsolutePosition = absolutePosition

        // Get current pixel offset.
        var dragVal: Float = positionOffsetPixels.toFloat()

        // If page has changed...
        if (lastPagePosition != position) {

            // Figure out what item should be current for secondary.
            var secondaryItem = absolutePosition / width
            if (!scrollingLeft) {
                secondaryItem += 1
            }

            // Set secondary current item.
            secondary.setCurrentItem(secondaryItem, true)

            // Adjust drag value by page delta * page width.
            dragVal += (position - lastPagePosition) * getPageWidth(primary)

            // Update last page position.
            lastPagePosition = position
        }

        // Get drag delta * multiplier.
        dragVal = (lastScrollOffset - dragVal) * getScrollMultiplier()

        // Fake drag secondary.
        secondary.fakeDragBy(dragVal)

        // Update last scroll value.
        lastScrollOffset = positionOffsetPixels
    }

    override fun onPageSelected(position: Int) {
        lastPagePosition = position
    }

    /**
     * Fake drag on secondary viewpager should be multiplied
     * by the ratio of the widths of the pages of both pagers.
     */
    private fun getScrollMultiplier(): Float {
        return getPageWidth(secondary).toFloat() / getPageWidth(primary)
    }

    /**
     * Get the width of a page from a viewpager.
     */
    private fun getPageWidth(pager: ViewPager): Int {
        return (pager.width - abs(pager.pageMargin))
    }
}
