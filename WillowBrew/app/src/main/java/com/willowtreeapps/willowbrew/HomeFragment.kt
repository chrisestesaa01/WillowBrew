package com.willowtreeapps.willowbrew

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment

import android.support.v4.view.ViewPager
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willowtreeapps.willowbrew.databinding.FragmentHomeBinding
import com.willowtreeapps.willowbrew.di.injectViewModel
import java.util.concurrent.atomic.AtomicReference


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var binding: FragmentHomeBinding? = null


    private val viewModel by injectViewModel { BeveragePageViewModel() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val sfm = activity?.supportFragmentManager

        sfm?.let {
            this.context?.let { c ->
            val pagerAdapter = BeveragePagerAdapter(c, it)
                val cardAdapter = BeverageCardPagerAdapter(c, it);
            initPager(pagerAdapter, cardAdapter)

                viewModel.getBevs().observe(this, Observer { bevs ->
                    if (bevs == null) return@Observer
                    //pagerAdapter.setItems(bevs)

                })

            }}

        val pageDots = binding?.pageDots
        pageDots?.setupWithViewPager(binding?.bevPager, true)

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false)
        return binding?.root
    }


    private val listener = object : ViewPager.OnPageChangeListener {

        private var jp: Int = -1

        override fun onPageScrollStateChanged(p0: Int) {
            if (ViewPager.SCROLL_STATE_IDLE == p0 && jp >= 0) {
                binding?.bevPager?.setCurrentItem(jp, false)
                jp = -1
            }
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        }

        override fun onPageSelected(p0: Int) {
            when (p0) {
                0 -> jp = 10
                10 -> jp = 1
                else -> {}
            }
        }

    }

    private fun initPager(pagerAdapter: BeveragePagerAdapter, cardAdapter: BeverageCardPagerAdapter) {
        val pager = binding?.bevPager ?: return
        pager.adapter = pagerAdapter

        val foo = binding?.cardPager ?: return
        foo.adapter = cardAdapter

        SyncPager2(pager, foo, 1.5f)
        SyncPager2(foo, pager, 0.5f)

//        pager.setPagerToSync(foo)
//        foo.setPagerToSync(pager)

//        val masterRef = AtomicReference<ViewPager>()
//
//        pager.addOnPageChangeListener(ParallaxOnPageChangeListener(pager, foo, masterRef))
//        foo.addOnPageChangeListener(ParallaxOnPageChangeListener(foo, pager, masterRef))
//



        pager.setPageTransformer(false, pagerAdapter)

        pager.currentItem = 0
        pager.offscreenPageLimit = 3
        pager.clipToPadding = false
        pager.pageMargin = -700

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

class SyncPager2(
        private val primary: ViewPager,
        private val secondary: ViewPager,
        private var dragMultiplier: Float = 1.0f
) : ViewPager.OnPageChangeListener
{

    init {

        primary.addOnPageChangeListener(this)
    }

    private fun getmult(): Float {

        val pw = primary.width + primary.pageMargin
        val sw = secondary.width + secondary.pageMargin
        return sw.toFloat() / pw
    }

    var lastPosition = 0
    var lastOffset = 0
    var scrollStartPosition = 0

    override fun onPageScrollStateChanged(state: Int) {
        if (primary.isFakeDragging) return
        when (state) {

            ViewPager.SCROLL_STATE_DRAGGING -> {
                secondary.beginFakeDrag()
            }
            ViewPager.SCROLL_STATE_SETTLING,
                ViewPager.SCROLL_STATE_IDLE -> {
                //scrollStartPosition = primary.currentItem
                lastOffset = 0
                lastPosition = primary.currentItem
                lastTotalPos = (lastPosition * (primary.width + primary.pageMargin))
                if (secondary.isFakeDragging) {
                    secondary.endFakeDrag()
                    secondary.setCurrentItem(primary.currentItem, true)
                    lastOffset = 0
                }
            }
        }
    }

    var lastTotalPos = 0

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (primary.isFakeDragging) return
        if (secondary.isFakeDragging) {

            val width = (primary.width + primary.pageMargin)
            var logStr = ("width: $width lastPos: $lastPosition pos: $position pix: $positionOffsetPixels")

            // Get total pixel offset from beginning of list.
            val totalPos = positionOffsetPixels + (position * width)

            // If total position is greater than last time, we are scrolling to the left.
            val scrollingLeft = (totalPos > lastTotalPos)
            lastTotalPos = totalPos




            logStr += " totalPos: $totalPos"


            var absOffset = positionOffsetPixels

            if (lastPosition != position) {


                var secondaryItem = totalPos / width
                if (!scrollingLeft) {
                    secondaryItem += 1
                }


                logStr += " sec item: $secondaryItem"

                    secondary.setCurrentItem(secondaryItem, false)


                absOffset += (position - lastPosition) * (primary.width + primary.pageMargin)

                lastPosition = position

//                if (absOffset < position) {
//                    secondary.setCurrentItem(position + 1, false)
//                    lastPosition = position + 1
//////                } else if ((position == (lastPosition + 1))) {
//////
//////                    secondary.setCurrentItem(position - 1, false)
//////                    lastPosition = position - 1
////                } else {
////                    secondary.setCurrentItem(position + 1, false)
//                } else {
//                    secondary.setCurrentItem(position, false)
//                    lastPosition = position
//                }

            }

            secondary.fakeDragBy((lastOffset - absOffset.toFloat()) * getmult())
            lastOffset = positionOffsetPixels

            log(logStr)

//            log("GIVEN: position: $position positionOffsetPixels: $positionOffsetPixels positionOffset: $positionOffset")
//            log("LAST:  position: $lastPosition positionOffsetPixels: $lastOffset")
//
//            val width = primary.measuredWidth + primary.pageMargin
//
//
//            var deltaPix = lastOffset - positionOffsetPixels
//
//
//            if (lastPosition != position) {
//                val abs = if (deltaPix < 0) {
//                    deltaPix * -1
//                } else {
//                    deltaPix
//                }
//
//                deltaPix = width - abs
//                if (position == (lastPosition - 1)) {
//                    secondary.setCurrentItem(position + 1, false)
//                } else {
//                    secondary.setCurrentItem(position, false)
//                }
//                    lastPosition = position
//
//            }
//
//
//
//            log("Delta pix: $deltaPix")
//
//            lastOffset = positionOffsetPixels
//
//            val drag = (deltaPix * getmult())
//
//
//            log("Delta: $deltaPix DRAG: $drag")
////            Log.d("DRAG", "\r\ndrag: $drag position: $position offset: $offset pixels: $pixels \r\ndeltapos: $deltaPos")
//            secondary.fakeDragBy(drag)
        }
    }

    override fun onPageSelected(position: Int) {
//        if (primary.isFakeDragging) return
//        if (!secondary.isFakeDragging) {
//            secondary.currentItem = position
//        }
        lastPosition = position
    }

    private fun log(msg: String) {
        Log.d("SYNC", msg)
    }
}


private class ParallaxOnPageChangeListener(
        /**
         * the viewpager that is being scrolled
         */
        private val viewPager: ViewPager,
        /**
         * the viewpager that should be synced
         */
        private val viewPager2: ViewPager, private val masterRef: AtomicReference<ViewPager>) : ViewPager.OnPageChangeListener {
    private var lastRemainder: Float = 0.toFloat()
    private var mLastPos = -1

    override fun onPageScrollStateChanged(state: Int) {
        val currentMaster = masterRef.get()
        if (currentMaster === viewPager2)
            return
        when (state) {
            ViewPager.SCROLL_STATE_DRAGGING -> if (currentMaster == null)
                masterRef.set(viewPager)
            ViewPager.SCROLL_STATE_SETTLING -> if (mLastPos != viewPager2.currentItem)
                viewPager2.setCurrentItem(viewPager.currentItem, false)
            ViewPager.SCROLL_STATE_IDLE -> {
                masterRef.set(null)
                viewPager2.setCurrentItem(viewPager.currentItem, false)
                mLastPos = -1
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (masterRef.get() === viewPager2)
            return
        if (mLastPos == -1)
            mLastPos = position
        val diffFactor = viewPager2.width.toFloat() / this.viewPager.width
        val scrollTo = this.viewPager.scrollX * diffFactor + lastRemainder
        val scrollToInt = if (scrollTo < 0) Math.ceil(scrollTo.toDouble()).toInt() else Math.floor(scrollTo.toDouble()).toInt()
        lastRemainder = scrollToInt - scrollTo
        if (mLastPos != viewPager.currentItem)
            viewPager2.setCurrentItem(viewPager.currentItem, false)
        viewPager2.scrollTo(scrollToInt, 0)

    }

    override fun onPageSelected(position: Int) {}
}