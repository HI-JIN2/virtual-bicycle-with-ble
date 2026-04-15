package com.eddy.mockebike.bluetooth

import android.os.Handler
import android.os.Looper
import com.eddy.mockebike.domain.BikeAction
import com.eddy.mockebike.domain.BikeBus

/** 포그라운드 서비스가 살아있는 동안, 주기적으로 metrics를 갱신한다. */
class BikeMetricsUpdater(
    private val bikeBus: BikeBus,
    private val heartRateNotificationManager: HeartRateNotificationManager,
    private val intervalMs: Long = 1000L,
) {

    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            bikeBus.dispatch(BikeAction.UpdateSpeed)
            bikeBus.dispatch(BikeAction.UpdateBattery)
            bikeBus.dispatch(BikeAction.UpdateDistance)

            heartRateNotificationManager.notify(bikeBus.state.value)
            handler.postDelayed(this, intervalMs)
        }
    }

    fun start() {
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }
}
