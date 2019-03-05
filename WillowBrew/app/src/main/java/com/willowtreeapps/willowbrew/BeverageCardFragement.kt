package com.willowtreeapps.willowbrew

import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.willowtreeapps.willowbrew.BeveragePageViewModel.BeverageModel.Type.*
import com.willowtreeapps.willowbrew.di.injectViewModel

class BeverageCardFragement : Fragment() {

    private var binding: com.willowtreeapps.willowbrew.databinding.FragmentBevCardBinding? = null

    private val viewModel by injectViewModel { BeveragePageViewModel() }

    companion object {
        fun newFragment(context: Context, position: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt("pos", position)
            return Fragment.instantiate(context, BeverageCardFragement::class.java.name, bundle)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        if (container == null) return null

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bev_card, container, false)


        val pos = this.arguments?.getInt("pos") ?: 0


        viewModel.getBevs().observe(this, Observer { model ->
            if (model == null) return@Observer
            val bev = model.getOrNull(pos) ?: return@Observer
            val dribbl = when (bev.type) {
                COFFEE -> R.drawable.coldbrew_selected
                KOMBUCHA -> R.drawable.kombucha_selected
                else -> R.drawable.beer_selected
            }
            context?.let {
                val dribbl2 = ContextCompat.getDrawable(it, dribbl)
                //binding?.itemInclude?.beverageImage?.setImageDrawable(dribbl2)
            }

            binding?.cardInclude?.bevName?.text = "$pos"
        })

        return binding?.root
    }
}