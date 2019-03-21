package com.willowtreeapps.willowbrew.beveragepage

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willowtreeapps.willowbrew.R
import com.willowtreeapps.willowbrew.databinding.BeverageGlassFragmentBinding
import com.willowtreeapps.willowbrew.di.injectViewModel

class BeverageGlassFragement : Fragment() {

    private var binding: BeverageGlassFragmentBinding? = null

    private val viewModel by injectViewModel { BeveragePageViewModel() }

    companion object {
        private const val POSITION = "position"
        private const val SCALE = "scale"
        fun newFragment(context: Context, position: Int, scale: Float): Fragment {
            val bundle = Bundle()
            bundle.putInt(POSITION, position)
            bundle.putFloat(SCALE, scale)
            return Fragment.instantiate(context, BeverageGlassFragement::class.java.name, bundle)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // If we have no container view then bail.
        if (container == null) return null

        // Otherwise, inflate our view.
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.beverage_glass_fragment,
                container,
                false
        )

        // Get page position.
        val pos = this.arguments?.getInt(POSITION) ?: 0

        // Get page model for the position from our viewmodel.
        viewModel.getBeveragePageModel(pos).observe(this, Observer { model ->
            if (model == null) return@Observer

            // Bind view.
            binding?.let { bind(model, it) }
        })

        // Set page scale.
        val scale = this.arguments?.getFloat(SCALE) ?: 0f
        binding?.itemInclude?.carouselItem?.setScaleBoth(scale)

        return binding?.root
    }

    /**
     * Bind page view.
     */
    private fun bind(model: BeveragePageModel, binding: BeverageGlassFragmentBinding
    ) {
        binding.itemInclude.percentValue.text = model.percent
        context?.let {
            val drawable = ContextCompat.getDrawable(it, model.glassDrawable)
            binding.itemInclude.beverageImage.setImageDrawable(drawable)
        }
    }
}
