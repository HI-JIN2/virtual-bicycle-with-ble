package com.eddy.mockebike.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddy.mockebike.MockEBikeApp
import com.eddy.mockebike.domain.BikeAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BikeViewModel : ViewModel() {

    private val bikeBus = MockEBikeApp.instance.bikeMetrics

    private val _uiState = MutableStateFlow(BikeUiState())
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    init {
        // Domain -> UI projection: ViewModel owns UiState.
        viewModelScope.launch {
            bikeBus.state.collect { s ->
                _uiState.update {
                    it.copy(
                        distance = s.distance,
                        speed = s.speed,
                        gear = s.gear,
                        battery = s.battery,
                        targetBattery = s.targetBattery,
                        proportionalFactor = s.proportionalFactor,
                    )
                }
            }
        }
    }

    fun changeProportionalFactor(proportionalFactor: Float) {
        viewModelScope.launch {
            bikeBus.dispatch(BikeAction.SetProportionalFactor(proportionalFactor))
        }
    }

    fun changeGear(gear: Int) {
        viewModelScope.launch {
            bikeBus.dispatch(BikeAction.SetGear(gear))
        }
    }

    fun changeTargetBattery(targetBattery: Float) {
        viewModelScope.launch {
            bikeBus.dispatch(BikeAction.SetTargetBattery(targetBattery))
        }
    }
}
