package com.willowtreeapps.willowbrew

import android.app.Application
import android.preference.PreferenceManager
import com.willowtreeapps.willowbrew.api.WillowBrewApi
import com.willowtreeapps.willowbrew.api.di.ApiModule
import com.willowtreeapps.willowbrew.di.ApplicationModule
import com.willowtreeapps.willowbrew.di.DaggerWillowBrewComponent
import com.willowtreeapps.willowbrew.di.WillowBrewComponent

class WillowBrewApplication : Application() {

    companion object {
        lateinit var willowBrewComponent: WillowBrewComponent
    }

    override fun onCreate() {
        super.onCreate()

        val api = ApiModule(
                PreferenceManager.getDefaultSharedPreferences(this),
                "https://kegscale-eafce.firebaseio.com"
        )

        willowBrewComponent = DaggerWillowBrewComponent
                .builder()
                .apiModule(api)
                .applicationModule(ApplicationModule(this))
                .build()


    }
}