package dev.rhy.mobile

import android.app.Application
import dev.rhy.mobile.utils.Cache

class BioApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Cache.instance().setContext(applicationContext)
    }
}