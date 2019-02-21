package com.willowtreeapps.willowbrew

import android.arch.lifecycle.ViewModel

class BeveragePageViewModel : ViewModel() {



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

    private val beverages: List<BeverageModel> = emptyList()

    fun getBev(position: Int):
}