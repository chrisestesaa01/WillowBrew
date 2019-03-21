package com.willowtreeapps.willowbrew.api.models

data class ApiData(
        val calibration: Calibration,
        val kegInfo: KegInfo,
        val weightData: Map<String, WeightReading>
) {

    data class Calibration(
            val calibrateFlag: Boolean,
            val calibrationPoint: Float,
            val calibrationValue: Float,
            val zeroFlag: Boolean,
            val zeroPoint: Int
    )

    data class KegInfo(
            val kegName: String,
            val lastChanged: String
    )

    data class WeightReading(
            val timeStamp: Long,
            val value: Double
    )
}
