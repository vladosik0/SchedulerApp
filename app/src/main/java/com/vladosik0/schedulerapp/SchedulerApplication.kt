package com.vladosik0.schedulerapp

import android.app.Application
import com.vladosik0.schedulerapp.data.AppContainer
import com.vladosik0.schedulerapp.data.DefaultAppContainer

class SchedulerApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}