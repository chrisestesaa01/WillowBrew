package com.willowtreeapps.willowbrew

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.willowtreeapps.willowbrew.api.WillowBrewApi
import com.willowtreeapps.willowbrew.beveragepage.BeveragePageModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import java.util.Arrays.asList



class BeveragePageViewModel @Inject constructor (
        private val controller: BeverageController,
        private val api: WillowBrewApi
) : ViewModel() {


    val pullToRefreshComplete: LiveData<Boolean>
    private val pullToRefreshCompleteData = MutableLiveData<Boolean>()

    init {
        pullToRefreshComplete = pullToRefreshCompleteData
    }


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

     private  suspend fun foo(): Int {
        val result = api.getWeightData("0").await()
         val result2 = result
         val foo = api.startZero("0", true).await()
         return 0
         return 0
     }


    fun onPullToRefresh() {

        val myJob = CoroutineScope(Dispatchers.IO).launch {
            val result = foo()
            withContext(Dispatchers.Main) {
                Log.d("RESULT", "$result")
            }
        }

        Completable.complete()
                .delay(1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pullToRefreshCompleteData.value = false }

    }


}
