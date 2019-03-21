package edu.ucsc.retap.retap.reminders

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.LayoutRes
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.App
import edu.ucsc.retap.retap.common.BaseActivity
import edu.ucsc.retap.retap.common.BasePresenter
import edu.ucsc.retap.retap.messages.adapter.MessagesAdapter
import edu.ucsc.retap.retap.messages.di.DaggerMessagesComponent
import edu.ucsc.retap.retap.messages.presenter.MessagesPresenter
import edu.ucsc.retap.retap.reminders.repository.RemindersRepository
import edu.ucsc.retap.retap.reminders.view.RemindersViewModule
import javax.inject.Inject

/**
 * Activity for the reminders feature. Users can enter a reminder, view them, and vibrate in Morse. They
 * can also double tap to delete the reminder and reminders will be persisted when the app closes.
 */
class RemindersActivity : BaseActivity() {
    @Inject lateinit var presenter: MessagesPresenter
    @Inject lateinit var adapter: MessagesAdapter
    private lateinit var remindersViewModule: RemindersViewModule
    private lateinit var remindersRepository: RemindersRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remindersViewModule = RemindersViewModule(this, object : RemindersViewModule.ComposeDelegate {
            override fun onCompose(text: String) {
                remindersRepository.addReminder(text)
            }
        })

        val applicationComponent = (applicationContext as App).objectGraph()
        remindersRepository = applicationComponent.remindersRepository()

        // Use Dagger to automatically create the presenter.
        // Pass in components that aren't defined in  our project and add them to the Builder definition.
        DaggerMessagesComponent.builder()
                .activity(this)
                .rootView(findViewById(R.id.root))
                .applicationComponent(applicationComponent)
                .itemLayoutId(R.layout.item_message_with_sender)
                .messagesSource(remindersRepository)
                .build()
                .inject(this)

        adapter
                .observeItemDoubleTap()
                .doOnNext {
                    remindersRepository.deleteReminder(it)
                }
                .subscribe()
    }

    @LayoutRes
    override fun layoutId(): Int = R.layout.activity_messages_list

    override fun toolbarTitleText(): String = resources.getString(R.string.reminders)

    override fun presenter(): BasePresenter = presenter

    override fun isActionButtonVisible(): Boolean = true

    override fun actionButtonDrawable(): Drawable? = resources.getDrawable(R.drawable.ic_compose, null)

    override fun onActionButtonClicked() = remindersViewModule.showComposeDialog()
}
