package com.willowtreeapps.willowbrew

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.willowtreeapps.willowbrew.beveragepage.BeveragePageModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeverageController @Inject constructor() {


    private val foo = BeveragePageModel(
            R.drawable.bev_beer_50,
            "50",
            "BEER",
            R.drawable.ic_cold_brew,
            "A Tasty ColdBrew",
            "01-01-19",
            true,
            "bev description here"
            )

    private val list = listOf(foo, foo, foo, foo, foo, foo, foo, foo, foo, foo)
    fun getBevs() = list
}