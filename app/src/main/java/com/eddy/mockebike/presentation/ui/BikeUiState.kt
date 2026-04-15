package com.eddy.mockebike.presentation.ui


data class BikeUiState(
    val distance: Float = 0.0f,
    val speed: Float = 0.0f,
    val gear: Int = 0,
    val battery: Float = 100f,
    val targetBattery: Float = 100f,
    val proportionalFactor: Float = 1.0f
)
