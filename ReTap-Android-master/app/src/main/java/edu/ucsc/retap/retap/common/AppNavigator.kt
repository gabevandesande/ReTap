package edu.ucsc.retap.retap.common

import android.content.Context
import android.content.Intent
import edu.ucsc.retap.retap.common.di.ApplicationScope
import edu.ucsc.retap.retap.compose.ComposeActivity
import edu.ucsc.retap.retap.contacts.model.Contact
import edu.ucsc.retap.retap.messages.MessagesActivity
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Delegate that handles navigation
 */
@ApplicationScope
class AppNavigator @Inject constructor(
        context: Context
) {
    private val contextRef = WeakReference(context)

    /**
     * Navigates to the compose screen.
     */
    fun navigateToCompose() {
        val context = contextRef.get() ?: return
        val intent = Intent(context, ComposeActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * Enters a specific conversation specified by the contact.
     * @param contact the given contact
     */
    fun navigateToConversation(contact: Contact) {
        val context = contextRef.get() ?: return
        val intent = Intent(context, MessagesActivity::class.java)
        val title = contact.displayName ?: contact.phoneNumber
        intent.putExtra(MessagesActivity.EXTRA_PHONE_NUMBER, contact.phoneNumber)
        intent.putExtra(MessagesActivity.EXTRA_TITLE, title)
        context.startActivity(intent)
    }
}
