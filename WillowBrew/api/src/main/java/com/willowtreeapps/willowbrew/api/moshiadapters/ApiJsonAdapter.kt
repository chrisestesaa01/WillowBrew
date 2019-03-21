package com.willowtreeapps.willowbrew.api.moshiadapters

import com.squareup.moshi.JsonAdapter
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
abstract class ApiJsonAdapter : JsonAdapter.Factory {
    companion object {
        val FACTORY: ApiJsonAdapter = KotshiApiJsonAdapter()
    }
}
