package com.eddy.mockebike.domain

/** 도메인 스냅샷(UI state 아님). */
data class BikeMetrics(
    val distance: Float = 0.0f,
    val speed: Float = 0.0f,
    val gear: Int = 0,
    val battery: Float = 100f,
    val targetBattery: Float = 100f,
    val proportionalFactor: Float = 1.0f
)
