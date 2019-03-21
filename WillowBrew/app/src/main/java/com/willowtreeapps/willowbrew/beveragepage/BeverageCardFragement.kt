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
import com.willowtreeapps.willowbrew.databinding.BeverageCardFragmentBinding
import com.willowtreeapps.willowbrew.di.injectViewModel

class BeverageCardFragement : Fragment() {

    private var binding: BeverageCardFragmentBinding? = null

    private val viewModel by injectViewModel { BeveragePageViewModel() }

    companion object {
        private const val POSITION = "position"
        fun newFragment(context: Context, position: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt(POSITION, position)
            return Fragment.instantiate(context, BeverageCardFragement::class.java.name, bundle)
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
                R.layout.beverage_card_fragment,
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

        return binding?.root
    }

    /**
     * Bind page view.
     */
    private fun bind(model: BeveragePageModel, binding: BeverageCardFragmentBinding) {
        binding.cardInclude.bevDate.text = model.changeDate
        binding.cardInclude.bevDescription.text = model.description
        binding.cardInclude.bevName.text = model.name
        binding.cardInclude.bevType.text = model.beverageType
        binding.cardInclude.refillWarning.visibility = when (model.needsRefill) {
            true -> View.VISIBLE
            else -> View.GONE
        }
        context?.let {
            val drawable = ContextCompat.getDrawable(it, model.cardDrawable)
            binding.cardInclude.triangleImage.setImageDrawable(drawable)
        }
    }
}
