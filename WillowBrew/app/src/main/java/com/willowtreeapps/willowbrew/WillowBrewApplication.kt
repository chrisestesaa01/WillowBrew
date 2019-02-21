package com.willowtreeapps.willowbrew

import android.app.Application
import com.willowtreeapps.willowbrew.di.WillowBrewComponent

class WillowBrewApplication : Application() {

    companion object {
        lateinit var willowBrewComponent: WillowBrewComponent
    }

    override fun onCreate() {
        super.onCreate()

       // willowBrewComponent = DaggerWillowBrewComponent

    }
}