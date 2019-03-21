package com.willowtreeapps.willowbrew.api

import com.willowtreeapps.willowbrew.api.models.ApiData
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface WillowBrewApi {

    /**
     * Returns a map of keg objects in the database. The keys of
     * this map can be used to access data for the individual kegs.
     */
    @GET("/scales.json?shallow=true")
    fun getKegCount(): Deferred<Map<String, Boolean>>

    /**
     * Get everything from the database.
     */
    @GET("/scales.json")
    fun getAll(): Deferred<List<ApiData>>

    /**
     * Get all data for a single scale.
     */
    @GET("/scales/{scaleIndex}.json")
    fun getScale(
            @Path("scaleIndex") scaleIndex: String
    ): Deferred<ApiData>

    /**
     * Get calibration data for a single scale.
     */
    @GET("/scales/{scaleIndex}/calibration.json")
    fun getCalibration(
            @Path("scaleIndex") scaleIndex: String
    )

    @PUT("scales/{scaleIndex}/calibration/zeroFlag.json")
    fun startZero(
            @Path("scaleIndex") scaleIndex: String,
            @Body flagVal: Boolean
    ): Deferred<Any>

    @GET("/scales/{scaleIndex}/weightData.json?orderBy=\"timeStamp\"&limitToLast=3")
    fun getWeightData(
            @Path("scaleIndex") scaleIndex: String
    ): Deferred<Map<String, ApiData.WeightReading>>

}
