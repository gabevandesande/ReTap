package edu.ucsc.retap.retap.contacts.model

/**
 * Contains information about a contact on the user's device.
 * @param phoneNumber the contact's phone number
 * @param displayName the contact's display name on the user's device
 */
data class Contact(
        val phoneNumber: String,
        val displayName: String?
)
