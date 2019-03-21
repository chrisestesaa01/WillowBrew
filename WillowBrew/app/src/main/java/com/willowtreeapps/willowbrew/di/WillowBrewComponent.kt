package com.willowtreeapps.willowbrew.di

import com.willowtreeapps.willowbrew.BeveragePageViewModel
import com.willowtreeapps.willowbrew.api.di.ApiModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    ApiModule::class
])
interface WillowBrewComponent {

    fun BeveragePageViewModel(): BeveragePageViewModel
}