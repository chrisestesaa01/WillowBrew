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

        SyncPager.synchronizePagers(pager, foo)



        pager.setPageTransformer(false, pagerAdapter)

        pager.currentItem = 0
        pager.offscreenPageLimit = 3
        pager.pageMargin = -700

        val foom = foo.getResources().getDimensionPixelOffset(R.dimen.bev_card_margin)
        //foo.pageMargin = foom + 1
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

