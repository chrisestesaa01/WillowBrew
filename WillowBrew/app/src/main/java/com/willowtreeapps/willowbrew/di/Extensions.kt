package com.willowtreeapps.willowbrew.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.willowtreeapps.willowbrew.WillowBrewApplication

/**
 * Injector for activity viewmodels.
 */
inline fun <reified VM : ViewModel> FragmentActivity.injectViewModel(
        noinline initializer: WillowBrewComponent.() -> VM
) = injectViewModelInternal(initializer, this)

/**
 * Injector for fragment viewmodels.
 */
inline fun <reified VM : ViewModel> Fragment.injectViewModel(
        noinline initializer: WillowBrewComponent.() -> VM
) = injectViewModelInternal(initializer, this)

/**
 * Injector for viewmodels that uses the ViewModelProviders to create
 * and store viewmodels. This method should not be called directly,
 * use the injector for Fragment or FragmentActivity instead.
 */
@PublishedApi
internal inline fun <reified VM : ViewModel> injectViewModelInternal(
        noinline initializer: WillowBrewComponent.() -> VM,
        receiver: Any
) = lazy(LazyThreadSafetyMode.NONE) {

    // Get vm from di component.
    val provider = WillowBrewApplication.willowBrewComponent.initializer()

    // Create factory to provide the vm.
    val factory = object: ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>) = provider as T
    }

    val vmProvider = when (receiver) {
        is Fragment -> ViewModelProviders.of(receiver, factory)
        is FragmentActivity -> ViewModelProviders.of(receiver, factory)
        else -> throw Exception("Context for viewmodel injection must be Fragment or FragmentActivity")
    }

    // get VM instance for this activity from vm providers.
    vmProvider.get(VM::class.java)
}
