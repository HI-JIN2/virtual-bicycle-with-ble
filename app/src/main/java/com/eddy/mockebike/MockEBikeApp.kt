package com.eddy.mockebike

import android.app.Application
import com.eddy.mockebike.domain.BikeBus

class MockEBikeApp : Application() {

    val bikeMetrics: BikeBus by lazy { BikeBus() }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @Volatile
        lateinit var instance: MockEBikeApp
            private set
    }
}
