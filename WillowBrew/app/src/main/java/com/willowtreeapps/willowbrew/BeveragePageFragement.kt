package com.willowtreeapps.willowbrew

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willowtreeapps.willowbrew.di.injectViewModel

class BeveragePageFragement : Fragment() {

    private var binding: com.willowtreeapps.willowbrew.databinding.FragmentBevPageBinding? = null

    private val viewModel by injectViewModel { BeveragePageViewModel() }

    companion object {
        fun newFragment(context: Context, position: Int, scale: Float): Fragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            bundle.putFloat("scale", scale)
            return Fragment.instantiate(context, BeveragePageFragement::class.java.name, bundle)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        if (container == null) return null

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bev_page, container, false)

        val scale = this.getArguments()?.getFloat("scale") ?: 0f

        binding?.itemInclude?.carouselItem?.setScaleBoth(scale)

        return binding?.root
    }
}