package com.willowtreeapps.willowbrew.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelProviderFactory @Inject constructor(
        private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Look for creator in creators map by model class.
        val creator: Provider<out ViewModel> = creators[modelClass]

                // If no creator was found, then look for any suitable creator.
                ?: creators.asSequence().find { (k, _) -> modelClass.isAssignableFrom(k) }?.value

                // If we still have no creator, then there's nothing we can do here.
                ?: throw IllegalArgumentException("Unknown model class $modelClass")

        try {

            // Get viewmodel.
            val vm = creator.get()

            // Return vm.
            return vm as T

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
