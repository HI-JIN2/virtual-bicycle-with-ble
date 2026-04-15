package com.eddy.mockebike.domain

/**
 * 단방향 액션 스트림(버스).
 * UI와 BLE가 모두 액션을 발행하고, Bus가 이를 reduce 해서 상태로 만든다.
 */
sealed interface BikeAction {
    data class SetGear(val gear: Int) : BikeAction
    data class SetProportionalFactor(val proportionalFactor: Float) : BikeAction
    data class SetTargetBattery(val targetBattery: Float) : BikeAction

    data object UpdateSpeed : BikeAction
    data object UpdateBattery : BikeAction
    data object UpdateDistance : BikeAction
}
