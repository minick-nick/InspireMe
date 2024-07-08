package com.example.inspireme

import android.app.Application
import com.example.inspireme.data.AppContainer
import com.example.inspireme.data.AppDataContainer

class InspireMeApplication : Application()  {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}