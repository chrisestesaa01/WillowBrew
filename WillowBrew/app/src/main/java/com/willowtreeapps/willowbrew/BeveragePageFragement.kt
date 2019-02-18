package com.willowtreeapps.willowbrew

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willowtreeapps.willowbrew.databinding.FragmentHomeBinding

class BeveragePageFragement : Fragment() {

    private var binding: com.willowtreeapps.willowbrew.databinding.BevPageFragmentBinding? = null

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

        binding = DataBindingUtil.inflate(inflater, R.layout.bev_page_fragment, container, false)

        val scale = this.getArguments()?.getFloat("scale") ?: 0f

        binding?.itemInclude?.carouselItem?.setScaleBoth(scale)

        return binding?.root
    }
}