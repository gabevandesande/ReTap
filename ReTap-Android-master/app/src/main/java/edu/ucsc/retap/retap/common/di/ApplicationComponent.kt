package edu.ucsc.retap.retap.common.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import edu.ucsc.retap.retap.common.AppNavigator
import edu.ucsc.retap.retap.morse.MorseInteractor
import edu.ucsc.retap.retap.reminders.repository.RemindersRepository

/**
 * Component for application wide dependencies.
 */
@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun appNavigator(): AppNavigator

    fun morseInteractor(): MorseInteractor

    fun remindersRepository(): RemindersRepository

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }
}
