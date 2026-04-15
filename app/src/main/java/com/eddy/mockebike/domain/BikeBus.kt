package com.eddy.mockebike.domain

import com.eddy.mockebike.utils.Util
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 단방향 상태 버스: 액션을 받아 도메인 상태(bike metrics)로 reduce 한다.
 * UI는 ViewModel에서 UiState로 투영(projection)한다.
 */
class BikeBus {

    private val _state = MutableStateFlow(BikeMetrics())
    val state: StateFlow<BikeMetrics> = _state.asStateFlow()

    fun dispatch(action: BikeAction) {
        _state.update { current ->
            when (action) {
                is BikeAction.SetGear -> current.copy(gear = action.gear)
                is BikeAction.SetProportionalFactor -> current.copy(
                    proportionalFactor = action.proportionalFactor
                )

                is BikeAction.SetTargetBattery -> {
                    val afterBattery = Util.calculateBattery(current.battery, action.targetBattery)
                    current.copy(battery = afterBattery, targetBattery = action.targetBattery)
                }

                BikeAction.UpdateSpeed -> {
                    val newSpeed = Util.calculateSpeed(
                        current.speed,
                        current.gear,
                        current.proportionalFactor
                    )
                    current.copy(speed = newSpeed)
                }

                BikeAction.UpdateBattery -> {
                    val newBattery = Util.calculateBattery(
                        current.battery,
                        current.targetBattery,
                    )
                    current.copy(battery = newBattery)
                }

                BikeAction.UpdateDistance -> {
                    val odo = Util.calculateOdo(
                        current.distance,
                        current.speed,
                    )
                    current.copy(distance = odo)
                }
            }
        }
    }
}
