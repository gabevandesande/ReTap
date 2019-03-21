package edu.ucsc.retap.retap.common.di

import android.content.Context
import android.os.Vibrator
import dagger.Module
import dagger.Provides

/**
 * Provides application wide dependencies.
 */
@Module
class ApplicationModule {
    @ApplicationScope
    @Provides
    fun provideVibrator(context: Context): Vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}
