package edu.ucsc.retap.retap.messages.model

import edu.ucsc.retap.retap.contacts.model.Contact

/**
 * Contains information about a SMS message.
 */
data class Message(
        val contact: Contact,
        val contents: String,
        val sent: Long
)
