package edu.ucsc.retap.retap.messages.di

import android.app.Activity
import android.support.annotation.LayoutRes
import android.view.View
import dagger.BindsInstance
import dagger.Component
import edu.ucsc.retap.retap.common.di.ActivityScope
import edu.ucsc.retap.retap.common.di.ApplicationComponent
import edu.ucsc.retap.retap.inbox.InboxActivity
import edu.ucsc.retap.retap.messages.MessagesActivity
import edu.ucsc.retap.retap.messages.data.MessagesSource
import edu.ucsc.retap.retap.reminders.RemindersActivity

/**
 * Component for Dagger to inject instance variables for MessagesActivity.
 */
@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [MessagesModule::class])
interface MessagesComponent {
    fun inject(inboxActivity: InboxActivity)

    fun inject(messagesActivity: MessagesActivity)

    fun inject(remindersActivity: RemindersActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun activity(activity: Activity): Builder

        @BindsInstance
        fun rootView(view: View): Builder

        @BindsInstance
        fun itemLayoutId(@LayoutRes itemLayoutId: Int): Builder

        @BindsInstance
        fun messagesSource(messagesSource: MessagesSource): Builder

        fun applicationComponent(applicationComponent: ApplicationComponent): Builder

        fun build(): MessagesComponent
    }
}
