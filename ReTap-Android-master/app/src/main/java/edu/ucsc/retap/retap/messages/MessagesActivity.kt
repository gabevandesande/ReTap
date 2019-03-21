package edu.ucsc.retap.retap.messages

import android.os.Bundle
import androidx.annotation.LayoutRes
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.App
import edu.ucsc.retap.retap.common.BaseActivity
import edu.ucsc.retap.retap.common.BasePresenter
import edu.ucsc.retap.retap.messages.data.SMSMessagesSource
import edu.ucsc.retap.retap.messages.di.DaggerMessagesComponent
import edu.ucsc.retap.retap.messages.presenter.MessagesPresenter
import javax.inject.Inject

/**
 * Activity for displaying a list of messages from the user's device.
 */
class MessagesActivity : BaseActivity() {
    companion object {
        const val EXTRA_PHONE_NUMBER = "e_phone_number"
        const val EXTRA_TITLE = "e_title"
    }

    @Inject lateinit var presenter: MessagesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val smsMessageSource = SMSMessagesSource(this)
        smsMessageSource.filterByPhone = intent.getStringExtra(EXTRA_PHONE_NUMBER)

        // Use Dagger to automatically create the presenter.
        // Pass in components that aren't defined in our project and add them to the Builder definition.
        DaggerMessagesComponent.builder()
                .activity(this)
                .rootView(findViewById(R.id.root))
                .applicationComponent((applicationContext as App).objectGraph())
                .itemLayoutId(R.layout.item_message)
                .messagesSource(smsMessageSource)
                .build()
                .inject(this)
    }

    @LayoutRes
    override fun layoutId(): Int = R.layout.activity_messages_list

    override fun toolbarTitleText(): String = intent.getStringExtra(EXTRA_PHONE_NUMBER)

    override fun presenter(): BasePresenter = presenter
}
