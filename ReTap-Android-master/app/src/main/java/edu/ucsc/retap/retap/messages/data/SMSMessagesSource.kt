package edu.ucsc.retap.retap.messages.data

import android.content.Context
import android.net.Uri
import edu.ucsc.retap.retap.contacts.interactor.ContactsHelper
import edu.ucsc.retap.retap.contacts.model.Contact
import edu.ucsc.retap.retap.messages.model.Message
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Exposes methods for fetching messages from the user's device.
 */
class SMSMessagesSource constructor(private val context: Context) : MessagesSource {
    companion object {
        private const val SMS_CONTENT_RESOLVER_URI = "content://sms/inbox"
    }

    var filterByPhone: String? = null
    private val senderPhoneNumberToContactMap = HashMap<String, Contact>()

    /**
     * Messages take a long time to get. Do it async and off the main thread with an observable.
     * @return the list of SMS messages on the user's device
     */
    override fun getMessages(): Single<List<Message>> {
        return Single.fromCallable {
            val smsMessages = ArrayList<Message>()

            val uri = Uri.parse(SMS_CONTENT_RESOLVER_URI)
            val cursor = context.contentResolver.query(uri, null, null, null, null)

            cursor ?: throw(NullPointerException("Cursor should not be null"))

            // Read the SMS data and store it in the list
            if (cursor.moveToFirst()) {
                for (i in 0 until cursor.count) {
                    val sender = cursor.getString(cursor
                            .getColumnIndexOrThrow("address")).toString()
                    val contents = cursor.getString(cursor
                            .getColumnIndexOrThrow("body")).toString()
                    val date = cursor.getString(cursor
                            .getColumnIndexOrThrow("date")).toString()

                    val contact = getContactForSender(sender)
                    val newMessage = Message(
                            contact,
                            contents,
                            date.toLong()
                    )
                    smsMessages.add(newMessage)
                    cursor.moveToNext()
                }
            }
            cursor.close()

            return@fromCallable if (filterByPhone != null) {
                smsMessages.filter { it.contact.phoneNumber == filterByPhone }
            } else {
                smsMessages
            }
        }
    }

    override fun sourceChangedObservable(): Observable<MessagesSource.Event> =
            Observable.empty()

    private fun getContactForSender(sender: String): Contact {
        val contactFromCache = senderPhoneNumberToContactMap[sender]
        if (contactFromCache != null) {
            return contactFromCache
        }
        val fetchedContact = ContactsHelper.getContact(context, sender)
        senderPhoneNumberToContactMap[sender] = fetchedContact
        return fetchedContact
    }
}
