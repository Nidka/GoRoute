package com.helkore.rutas.android

import android.app.Application
import com.helkore.rutas.android.di.networkModule
import com.helkore.rutas.android.di.repositoryModule
import com.helkore.rutas.android.di.useCaseModule
import com.helkore.rutas.android.di.viewModelModule
import com.helkore.rutas.android.notification.BusNotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RutasApp : Application() {
    override fun onCreate() {
        super.onCreate()
        BusNotificationHelper.createChannel(this)
        startKoin {
            androidContext(this@RutasApp)
            modules(networkModule, repositoryModule, useCaseModule, viewModelModule)
        }
    }
}
