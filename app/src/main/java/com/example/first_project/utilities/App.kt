package com.example.first_project.utilities

import android.app.Application
import com.example.first_project.KotlinCoroutinesActivity

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

     fun onPause() {
        super.onTerminate()
    }

     fun onResume() {
        super.onTerminate()
    }
}
