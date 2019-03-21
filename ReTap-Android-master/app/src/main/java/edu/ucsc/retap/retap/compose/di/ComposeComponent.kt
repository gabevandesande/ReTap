package edu.ucsc.retap.retap.compose.di

import android.app.Activity
import android.view.View
import dagger.BindsInstance
import dagger.Component
import edu.ucsc.retap.retap.common.di.ActivityScope
import edu.ucsc.retap.retap.compose.ComposeActivity

/**
 * Component for Dagger to inject instance variables for ComposeActivity.
 */
@ActivityScope
@Component
interface ComposeComponent {
    fun inject(target: ComposeActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: Activity): Builder

        @BindsInstance
        fun rootView(view: View): Builder

        fun build(): ComposeComponent
    }
}
