package com.willowtreeapps.willowbrew.di

import com.willowtreeapps.willowbrew.BeveragePageViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class
])
interface WillowBrewComponent {

    fun BeveragePageViewModel(): BeveragePageViewModel
}