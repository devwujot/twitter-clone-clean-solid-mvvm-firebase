package com.devwujot.hashtag

import android.app.Application
import androidx.multidex.MultiDex
import com.devwujot.hashtag.framework.di.repositoryModule
import com.devwujot.hashtag.framework.di.useCasesModule
import com.devwujot.hashtag.framework.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(listOf(repositoryModule, useCasesModule, viewModelModule))
        }
    }
}