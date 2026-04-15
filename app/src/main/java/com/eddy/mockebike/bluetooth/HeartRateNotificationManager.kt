package com.eddy.mockebike.bluetooth

import com.eddy.mockebike.domain.BikeBus
import com.eddy.mockebike.domain.BikeMetrics
import com.eddy.mockebike.utils.Util.floatTo4ByteArray
import com.eddy.mockebike.utils.Uuid
import timber.log.Timber
import java.nio.ByteBuffer

class HeartRateNotificationManager(
    private val gattServerManager: GattServerManager,
    private val bikeBus: BikeBus,
) {

    fun notify(metrics: BikeMetrics) {
        if (gattServerManager.registeredDevices.isEmpty()) return

        val buffer = ByteBuffer.allocate(13) // 4 + 4 + 1 + 4
        buffer.put(floatTo4ByteArray(metrics.distance))
        buffer.put(floatTo4ByteArray(metrics.speed))
        buffer.put(metrics.gear.toByte())
        buffer.put(floatTo4ByteArray(metrics.battery))
        notifyHeartRate(buffer.array())
    }

    private fun notifyHeartRate(heartRate: ByteArray) {
        Timber.i(
            "Sending heart rate update to ${gattServerManager.registeredDevices.size} subscribers"
        )
        val heartRateCharacteristic = gattServerManager.bluetoothGattServer
            ?.getService(Uuid.HEART_RATE_SERVICE)
            ?.getCharacteristic(Uuid.HEART_RATE_MEASUREMENT)
        heartRateCharacteristic?.value = heartRate

        gattServerManager.notifyCharacteristicChanged(heartRateCharacteristic!!)
    }
}
