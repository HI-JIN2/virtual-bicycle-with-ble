package com.eddy.mockebike.domain

/**
 * Unidirectional action stream ("bus").
 * UI and BLE both dispatch actions; Store reduces them into state.
 */
sealed interface BikeAction {
    data class SetGear(val gear: Int) : BikeAction
    data class SetProportionalFactor(val proportionalFactor: Float) : BikeAction
    data class SetTargetBattery(val targetBattery: Float) : BikeAction

    data object UpdateSpeed : BikeAction
    data object UpdateBattery : BikeAction
    data object UpdateDistance : BikeAction
}
