package com.willowtreeapps.willowbrew

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*

import com.willowtreeapps.willowbrew.beveragepage.BeverageCardPagerAdapter
import com.willowtreeapps.willowbrew.beveragepage.BeverageGlassPagerAdapter
import com.willowtreeapps.willowbrew.databinding.HomePageFragmentBinding
import com.willowtreeapps.willowbrew.di.injectViewModel
import java.util.zip.Inflater

class HomeFragment : Fragment() {

    private var binding: HomePageFragmentBinding? = null

    private val viewModel by injectViewModel { BeveragePageViewModel() }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.home_page_fragment,
                container,
                false
        )


        initPagers()
        initRefreshSwiper()
        initPageDots()

        return binding?.root
    }

//    private fun initializeActionBar() {
//
//        val activity = this.activity ?: return
//        val binding = this.binding ?: return
//        if (activity !is AppCompatActivity) return
//
//            activity.setSupportActionBar(binding.toolbar)
//
//        setHasOptionsMenu(true)
//
//        // Enable home button.
//        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        // Set appropriate icon on home button.
//        val drawable = if (showBack) R.drawable.ic_arrow_back else R.drawable.ic_menu
//        supportActionBar?.setHomeAsUpIndicator(drawable)
//
//        // Show actionbar.
//        supportActionBar?.show()
//    }

    private fun initPagers() {

        // Get fragment manager and context.
        val sfm = activity?.supportFragmentManager ?: return
        val context = this.context ?: return

        // Get pagers.
        val glassPager = binding?.layoutInclude?.glassPager ?: return
        val cardPager = binding?.layoutInclude?.cardPager ?: return

        // Create pager adapters.
        val pagerAdapter = BeverageGlassPagerAdapter(context, sfm)
        val cardAdapter = BeverageCardPagerAdapter(context, sfm)

        // Set adapters for pagers.
        glassPager.adapter = pagerAdapter
        cardPager.adapter = cardAdapter

        // Synchronize the two pagers.
        SyncPager.synchronizePagers(glassPager, cardPager)

        // Set page transformer for the glass pager.
        glassPager.setPageTransformer(false, pagerAdapter)

        // Set up glass pager to show side items.
        glassPager.currentItem = 0
        glassPager.offscreenPageLimit = 3

        val bevPagerMagin = cardPager
                .getResources()
                .getDimensionPixelOffset(R.dimen.beverage_glass_page_margin)

        glassPager.pageMargin = -bevPagerMagin

        // Bind pager item count to viewmodel.
        viewModel.getBeverageListSize().observe(this, Observer { count ->
            cardAdapter.count = (count ?: 0)
            pagerAdapter.count = (count ?: 0)
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        menuInflater?.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_apt_view_switcher -> {
                return true
            }
            R.id.menu_sort_button -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }



    private fun initRefreshSwiper() {

        // Block refresh swipe when pagers are scrolling.
        binding?.refreshSwiper?.let {
            it.blockWhenScrolling(binding?.layoutInclude?.glassPager)
            it.blockWhenScrolling(binding?.layoutInclude?.cardPager)
        }

        // Call to vm when refresh swipe occurs.
        binding?.refreshSwiper?.setOnRefreshListener {
            viewModel.onPullToRefresh()
        }

        // Listen to vm for signal that refresh is complete.
        viewModel.pullToRefreshComplete.observe( this, Observer { refreshComplete ->
            binding?.refreshSwiper?.isRefreshing = (true == refreshComplete)
        })
    }

    private fun initPageDots() {
        val pageDots = binding?.layoutInclude?.pageDots
        pageDots?.setupWithViewPager(
                binding?.layoutInclude?.glassPager,
                true
        )
    }
}
