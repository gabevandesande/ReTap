package edu.ucsc.retap.retap.common

import android.app.Application
import edu.ucsc.retap.retap.common.di.ApplicationComponent
import edu.ucsc.retap.retap.common.di.DaggerApplicationComponent

/**
 * Application class that initializes the Dagger application component.
 */
class App : Application() {
    companion object {
        private lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
                .builder()
                .context(this)
                .build()
    }

    fun objectGraph(): ApplicationComponent = applicationComponent
}
