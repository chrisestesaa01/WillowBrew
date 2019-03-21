package com.willowtreeapps.willowbrew.api.moshiadapters

import android.util.Log
import com.squareup.moshi.*

/**
 * Adapter to use custom string field 'apiValue' of enums for json (de)serialization. This
 * adapter will work for numerical json data as well since moshi will deserialize number data
 * to string.
 */
class EnumAdapter<T : EnumAdapter.AdaptableEnum>(private val adaptedEnum: Class<T>) : JsonAdapter<T>(){

    companion object {

        private val TAG = EnumAdapter::class.java.simpleName

        val FACTORY: JsonAdapter.Factory = Factory { type, _, _ ->

            // If the given type implements the interface defined here...
            val rawType = Types.getRawType(type)
            if (rawType.interfaces.contains(AdaptableEnum::class.java)) {

                // Then create and return an adapter for it.
                return@Factory EnumAdapter(rawType as Class<AdaptableEnum>)
            }

            // Otherwise return null.
            null
        }
    }

    // Interface defining the enum field to use for API adaption.
    interface AdaptableEnum { val apiValue : String }

    override fun fromJson(reader: JsonReader?): T? {

        // Look for enum value whose 'apiValue' field matches the name from the reader.
        val name = reader?.nextString() ?: ""
        val result = adaptedEnum.enumConstants.find { it.apiValue == name }

        // If not found, then log error.
        if (result == null) {
            val expected = adaptedEnum.enumConstants.map { it.apiValue }
            val msg = "Expected one of $expected but was $name at path ${reader?.path}"
            Log.e(TAG, msg)
        }

        return result
    }

    override fun toJson(writer: JsonWriter?, value: T?) {

        // Get the 'apiValue' field data from the enum.
        val data = value?.apiValue

        // Write the field data.
        if (data != null) { writer?.value(data) } else { writer?.nullValue() }
    }
}
