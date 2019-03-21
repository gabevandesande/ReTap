package edu.ucsc.retap.retap.inbox

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.LayoutRes
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.App
import edu.ucsc.retap.retap.common.BaseActivity
import edu.ucsc.retap.retap.common.BasePresenter
import edu.ucsc.retap.retap.inbox.presenter.InboxPresenter
import edu.ucsc.retap.retap.messages.data.SMSMessagesSource
import edu.ucsc.retap.retap.messages.di.DaggerMessagesComponent
import edu.ucsc.retap.retap.permissions.PermissionsInteractor
import javax.inject.Inject

/**
 * Displays a list of the user's SMS conversations and a preview of the text in Morse code for each.
 *
 * The user can select a conversation to go into by tapping, or they can long click the preview to toggle between plain
 * text. THe user is also able to navigate through the list and select items with the volume up and down keys. If an
 * item has been selected for more than a few seconds, the user is automatically taken to the selected conversation.
 */
class InboxActivity : BaseActivity() {
    @Inject lateinit var permissionsInteractor: PermissionsInteractor
    @Inject lateinit var presenter: InboxPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use Dagger to automatically create the presenter.
        // Pass in components that aren't defined in our project and add them to the Builder definition.
        DaggerMessagesComponent.builder()
                .activity(this)
                .rootView(findViewById(R.id.root))
                .applicationComponent((applicationContext as App).objectGraph())
                .itemLayoutId(R.layout.item_message_with_sender)
                .messagesSource(SMSMessagesSource(this))
                .build()
                .inject(this)
    }

    @LayoutRes
    override fun layoutId(): Int = R.layout.activity_messages_list

    override fun toolbarTitleText(): String = resources.getString(R.string.inbox)

    override fun isActionButtonVisible(): Boolean = true

    override fun actionButtonDrawable(): Drawable? = resources.getDrawable(R.drawable.ic_compose, null)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) =
            permissionsInteractor.onRequestPermissionsResult(requestCode, grantResults)

    override fun presenter(): BasePresenter = presenter
}
