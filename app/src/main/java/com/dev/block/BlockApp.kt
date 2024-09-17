package com.dev.block

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BlockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("D0474EDED41DA4BFD1BA167DF0689F74")).build()
        )
    }
}