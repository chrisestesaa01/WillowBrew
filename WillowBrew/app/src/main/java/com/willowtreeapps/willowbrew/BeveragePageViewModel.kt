package com.willowtreeapps.willowbrew

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.willowtreeapps.willowbrew.beveragepage.BeveragePageModel
import javax.inject.Inject

class BeveragePageViewModel @Inject constructor (
        private val controller: BeverageController
) : ViewModel() {




    fun getBeveragePageModel(position: Int): LiveData<BeveragePageModel> {

        val data = MutableLiveData<BeveragePageModel>()
        data.value = controller.getBevs().getOrNull(position)
        return data
    }

    fun getBeverageListSize() : LiveData<Int> {
        val data = MutableLiveData<Int>()
        data.value = controller.getBevs().size
        return data
    }

}
