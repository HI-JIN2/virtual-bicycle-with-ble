package com.eddy.mockebike.bluetooth

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.eddy.mockebike.MockEBikeApp
import com.eddy.mockebike.domain.BikeBus

class BluetoothService : Service() {

    private lateinit var bikeBus: BikeBus

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var advertisingManager: AdvertisingManager
    private lateinit var gattServerManager: GattServerManager
    private lateinit var heartRateNotificationManager: HeartRateNotificationManager
    private lateinit var bikeMetricsUpdater: BikeMetricsUpdater
    private lateinit var bluetoothStateReceiver: BluetoothStateReceiver

    override fun onCreate() {
        super.onCreate()

        bikeBus = (application as MockEBikeApp).bikeMetrics

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        //블루투스 서비스가 거대해지는 것을 막기 위해서 단일 책임원칙에 따른 관심사 분리를 했음
        advertisingManager = AdvertisingManager(bluetoothAdapter)
        gattServerManager = GattServerManager(this, bluetoothManager, bikeBus)
        heartRateNotificationManager = HeartRateNotificationManager(gattServerManager, bikeBus)
        bikeMetricsUpdater = BikeMetricsUpdater(bikeBus, heartRateNotificationManager)
        bluetoothStateReceiver = BluetoothStateReceiver(
            this,
            advertisingManager,
            gattServerManager,
            bikeMetricsUpdater
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification() // Notification 생성
        startForeground(1, notification) // Foreground 서비스 시작

        initializeBluetooth()

        return START_STICKY // 서비스가 중단되었을 경우 자동으로 다시 시작
    }

    private fun createNotification(): Notification {
        val channelId = "bluetooth_service_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Bluetooth Service",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Bluetooth Service")
            .setContentText("Bluetooth service is running.")
//            .setSmallIcon(R.drawable.ic_bluetooth) // 적절한 아이콘 설정
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    fun initializeBluetooth() {
        if (!checkBluetoothSupport(bluetoothAdapter)) {//블루투스 미지원 기기 거름
            return
        }

        bluetoothStateReceiver.register()

        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        } else {
            startBluetoothServices()
        }
    }

    private fun startBluetoothServices() {
        advertisingManager.startAdvertising()
        gattServerManager.startServer()
        bikeMetricsUpdater.start()
    }

    fun cleanup() {
        bluetoothStateReceiver.unregister()
        gattServerManager.stopServer()
        advertisingManager.stopAdvertising()
        bikeMetricsUpdater.stop()
    }

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }

    private fun checkBluetoothSupport(bluetoothAdapter: BluetoothAdapter?): Boolean {
        return bluetoothAdapter != null
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BluetoothService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, BluetoothService::class.java)
            context.stopService(intent)
        }
    }

}
