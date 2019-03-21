package edu.ucsc.retap.retap.contacts.interactor

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import edu.ucsc.retap.retap.contacts.model.Contact

/**
 * Contains methods that facilitate getting Contacts.
 */
object ContactsHelper {

    /**
     * @param context the current context
     * @param phoneNumber the user's phone number.
     * @return a contact object given a user's phone number.
     */
    fun getContact(context: Context, phoneNumber: String): Contact {
        val contentResolver = context.contentResolver
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        var displayName: String? = null
        if (cursor != null) {
            while (cursor.moveToNext()) {
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
            cursor.close()
        }

        return Contact(phoneNumber, displayName)
    }
}
