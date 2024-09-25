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
    }
}