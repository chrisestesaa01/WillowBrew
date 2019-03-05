package com.willowtreeapps.willowbrew

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import javax.inject.Inject

class BeveragePageViewModel @Inject constructor (
        private val controller: BeverageController
) : ViewModel() {



    class BeverageModel(
            val type: Type,
            val percent: Int
    ) {
        enum class Type {
            BEER,
            COFFEE,
            KOMBUCHA,
            OTHER
        }

    }



    fun getBevs() = controller.bevs

}
