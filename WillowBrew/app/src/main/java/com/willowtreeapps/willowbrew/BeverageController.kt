package com.willowtreeapps.willowbrew

import android.arch.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeverageController @Inject constructor() {


    private val foo = BeveragePageViewModel.BeverageModel(
            BeveragePageViewModel.BeverageModel.Type.BEER,
            67
    )
    private val beverages: List<BeveragePageViewModel.BeverageModel> = listOf(foo, foo, foo, foo, foo, foo, foo, foo, foo)

    val bevs = MutableLiveData<List<BeveragePageViewModel.BeverageModel>>()
    init {
        bevs.value = beverages
    }

    fun getBeverage(index: Int): BeveragePageViewModel.BeverageModel? {
        return beverages.getOrNull(index)
    }

}